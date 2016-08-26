package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXLoanPaymentSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		Account loanAccount = XPersistence.getManager().find(Account.class, getCreditAccount().getAccountId());
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
			
			//InsurancePayment
			if (quota.getInsurance().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsurance();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_RECEIVABLE_CATEGORY));
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			//InsuranceMortgagePayment
			if (quota.getInsurance().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsurance();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_RECEIVABLE_CATEGORY));
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			//DefaultInteresPayment
			if (quota.getDefaultInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getDefaultInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY));
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//InteresPayment
			if (quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_PR_CATEGORY));
				ta.setRemark(XavaResources.getString("interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
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
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount, quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY));
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
				
			}
			
		}
				
		return transactionAccounts;

	}
	
	public void extraValidations() throws Exception
	{
		BigDecimal totalOverdue = (BigDecimal)getView().getRoot().getSubview("creditAccount").getValue("totalOverdueBalance");
		BigDecimal transactionValue = getValue();
		
		if (totalOverdue.compareTo(BigDecimal.ZERO)<=0)
			throw new OperativeException("balance_due_is_zero");
		
		if (transactionValue.compareTo(totalOverdue)>0)
			throw new OperativeException("amount_to_be_paid_is_greater_than_the_overdue_balance");

	}
	
	@SuppressWarnings("unchecked")
	public void postSaveAction(Transaction transaction) throws Exception
	{
		Date paymentDate = CompanyHelper.getCurrentAccountingDate();
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account loanAccount = transaction.getCreditAccount();
			
			List<AccountOverdueBalance> quotas = XPersistence.getManager()
					.createQuery("SELECT o FROM AccountOverdueBalance o "
							+ "WHERE o.accountId = :accountId "
							+ "ORDER BY o.subaccount")
					.setParameter("accountId", loanAccount.getAccountId())
					.getResultList();
			
			for (AccountOverdueBalance quota: quotas)
			{				
				if (AccountLoanHelper.getBalanceByQuota(quota.getAccountId(), quota.getSubaccount()).compareTo(BigDecimal.ZERO)<=0)
				{
					AccountPaytable ap = (AccountPaytable)XPersistence.getManager()
					.createQuery("SELECT o FROM AccountPaytable o "
							+ "WHERE o.accountId = :accountId "
							+ "AND o.subaccount = :subaccount")
					.setParameter("accountId", loanAccount.getAccountId())
					.setParameter("subaccount", quota.getSubaccount())
					.getSingleResult();
					ap.setPaymentDate(paymentDate);
					XPersistence.getManager().merge(ap);
					System.out.println("Update AccountPaytable "+loanAccount.getAccountId()+"|"+ap.getSubaccount()+" to cancel");
				}
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
			.setParameter("accountId", loanAccount.getAccountId())
			.executeUpdate();
			
		}
		
	}
}
