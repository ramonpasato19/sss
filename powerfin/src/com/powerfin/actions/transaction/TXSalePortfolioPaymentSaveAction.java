package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXSalePortfolioPaymentSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account creditAccount = getCreditAccount();
		Account loanAccount = XPersistence.getManager().find(Account.class, getDebitAccount().getAccountId());
		BigDecimal transactionValue = getValue();
		BigDecimal valueToApply = BigDecimal.ZERO;
		TransactionAccount ta = null;
		
		List<AccountOverdueBalance> quotas = XPersistence.getManager()
				.createQuery("SELECT o FROM AccountOverdueBalance o "
						+ "WHERE o.accountId = :accountId "
						+ "ORDER BY o.subaccount")
				.setParameter("accountId", loanAccount.getAccountId())
				.getResultList();
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (quotas==null || quotas.isEmpty())
    		throw new OperativeException("payment_not_processed_with_out_overdue_balances");

		//capital
		for (AccountOverdueBalance quota: quotas)
		{
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
									
			//InteresPayment
			if (quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_IN_CATEGORY));
				ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//CapitalPayment
			if (quota.getCapital().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getCapital();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.SALE_CAPITAL_CATEGORY));
				ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
				
			}
			
		}
				
		return transactionAccounts;

	}
	
	public void extraValidations() throws Exception
	{
		BigDecimal totalOverdue = (BigDecimal)getView().getRoot().getSubview("debitAccount").getValue("totalOverdueBalance");
		BigDecimal transactionValue = getValue();
		
		if (totalOverdue.compareTo(BigDecimal.ZERO)<=0)
			throw new OperativeException("balance_due_is_zero");
		
		if (transactionValue.compareTo(totalOverdue)>0)
			throw new OperativeException("amount_to_be_paid_is_greater_than_the_overdue_balance");

	}
	
	@SuppressWarnings("unchecked")
	public void postSaveAction(Transaction transaction) throws Exception
	{
		
		Date currentDate = CompanyHelper.getCurrentAccountingDate();
		if (TransactionHelper.isFinancialSaved(transaction))
		{			
			List<AccountSoldPaytable> accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountSoldPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getDebitAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("debitOrCredit", "D")
					.getResultList();
			
			for (AccountSoldPaytable accountPaytable: accountPaytables)
			{				
				BigDecimal capitalBalance = AccountLoanHelper.getBalanceByQuotaAndCategory(accountPaytable.getAccountId(), accountPaytable.getSubaccount(), CategoryHelper.SALE_CAPITAL_CATEGORY);			
				
				if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
				{
					accountPaytable.setPaymentDate(currentDate);
					System.out.println("Update AccountSoldPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" to cancel");
				}
				accountPaytable.setLastPaymentDate(currentDate);
				XPersistence.getManager().merge(accountPaytable);
			}
			
			/*
			if (AccountLoanHelper.getBalanceByLoanAccount(loanAccount.getAccountId()).compareTo(BigDecimal.ZERO)<=0)
			{
				Account persisAccount = XPersistence.getManager().find(Account.class, loanAccount.getAccountId());
				persisAccount.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_CANCEL));
				persisAccount.setCancellationDate(paymentDate);
				AccountHelper.updateAccount(persisAccount);
			}
			*/
			XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
					+ "WHERE o.accountId=:accountId ")
			.setParameter("accountId", transaction.getDebitAccount().getAccountId())
			.executeUpdate();
			
		}
		
	}
}
