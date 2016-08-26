package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.types.Types.*;

public class TXPurchasePortfolioPaymentSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		AccountLoan loanAccount = XPersistence.getManager().find(AccountLoan.class, getCreditAccount().getAccountId());
		Account disbursementAccount = loanAccount.getDisbursementAccount();
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

		if (loanAccount.getDisbursementAccount()==null)
			throw new OperativeException("disbursement_account_not_found");
		
		if (!loanAccount.getDisbursementAccount().getAccountId().equals(debitAccount.getAccountId()))
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, transactionValue, transaction);
			ta.setRemark(XavaResources.getString("transfer_to_customer_for_payment_loan", loanAccount.getAccountId()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(disbursementAccount, transactionValue, transaction);
			ta.setRemark(XavaResources.getString("transfer_from_broker_for_payment_loan", loanAccount.getAccountId()));
			transactionAccounts.add(ta);
		}
		else
			disbursementAccount = debitAccount;
		
		for (AccountOverdueBalance quota: quotas)
		{
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//ReceivableFeePayment
			if (quota.getReceivableFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getReceivableFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.RECEIVABLE_FEE_RE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("receivable_fee_payment", loanAccount.getAccountId()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("receivable_fee_payment", loanAccount.getAccountId()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//LegalFeePayment
			if (quota.getLegalFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getLegalFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.LEGAL_FEE_RE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("legal_fee_payment", loanAccount.getAccountId()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("legal_fee_payment", loanAccount.getAccountId()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//CollectionFee
			//Si no cubre el valor completo de los cargos de cobranza termina la prelacion
			if (quota.getCollectionFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getCollectionFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					break;
					//valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.COLLECTION_FEE_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//DefaultInterest
			//Si no cubre el valor completo de la mora termina la prelacion
			if (quota.getDefaultInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getDefaultInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					break;
					//valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//Insurance
			if (quota.getInsurance().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsurance();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_RECEIVABLE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//InsuranceMortgage
			if (quota.getInsuranceMortgage().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsuranceMortgage();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_RECEIVABLE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}

			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//Interest
			if (quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_PR_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("interest_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
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
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loanAccount.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", loanAccount.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
				
			}
			
		}
				
		return transactionAccounts;

	}
	
	public void extraValidations() throws Exception
	{
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalances(getCreditAccount());
		BigDecimal totalOverdue = BigDecimal.ZERO;
		BigDecimal minToPayment = BigDecimal.ZERO;
		BigDecimal transactionValue = getValue();
		
		for (AccountOverdueBalance balance:overdueBalances)
		{
			totalOverdue=totalOverdue.add(balance.getTotal());
			minToPayment=minToPayment.add(balance.getReceivableFee())
					.add(balance.getLegalFee())
					.add(balance.getCollectionFee())
					.add(balance.getDefaultInterest());
		}
		
		if (totalOverdue.compareTo(BigDecimal.ZERO)<=0)
			throw new OperativeException("balance_due_is_zero");
		
		if (transactionValue.compareTo(totalOverdue)>0)
			throw new OperativeException("amount_to_be_paid_is_greater_than_the_overdue_balance");
		/*
		if (transactionValue.compareTo(minToPayment)<0)
			throw new OperativeException("the_minimum_value_to_pay_is", minToPayment);
			*/
	}
	
	@SuppressWarnings("unchecked")
	public void postSaveAction(Transaction transaction) throws Exception
	{
		Date currentDate = CompanyHelper.getCurrentAccountingDate();
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			List<AccountPaytable> accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				BigDecimal capitalBalance = AccountLoanHelper.getBalanceByQuotaAndCategory(accountPaytable.getAccountId(), accountPaytable.getSubaccount(), CategoryHelper.CAPITAL_CATEGORY);			
				
				if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
				{
					accountPaytable.setPaymentDate(currentDate);
					System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" to cancel");
				}
				accountPaytable.setLastPaymentDate(currentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment = "+currentDate);
			}
			
			accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.category.categoryId = :categoryId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("categoryId", CategoryHelper.COLLECTION_FEE_IN_CATEGORY)
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				accountPaytable.setLastPaymentDateCollection(currentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment collection = "+currentDate);
			}
			
			accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.category.categoryId = :categoryId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("categoryId", CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY)
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				accountPaytable.setLastPaymentDateDefaultInterest(currentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment default interest = "+currentDate);
			}
			/*
			if (AccountLoanHelper.getBalanceByLoanAccount(transaction.getCreditAccount().getAccountId()).compareTo(BigDecimal.ZERO)<=0)
			{
				Account persistAccount = XPersistence.getManager().find(Account.class, transaction.getCreditAccount().getAccountId());
				persistAccount.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_CANCEL));
				persistAccount.setCancellationDate(currentDate);
				AccountHelper.updateAccount(persistAccount);
			}
			*/
			XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
					+ "WHERE o.accountId=:accountId ")
			.setParameter("accountId", transaction.getCreditAccount().getAccountId())
			.executeUpdate();
			
		}
		
	}
}
