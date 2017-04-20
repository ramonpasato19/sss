package com.powerfin.actions.batch;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class LoanInsuranceBatchSaveAction implements IBatchSaveAction  {

	public LoanInsuranceBatchSaveAction()
	{
		
	}
	
	public Transaction getTransaction(BatchProcess batchProcess, BatchProcessDetail batchProcessDetail) throws Exception 
	{
		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(batchProcess.getBatchProcessType().getTransactionModule());
		transaction.setTransactionStatus(batchProcess.getBatchProcessType().getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(BigDecimal.ZERO);
		transaction.setRemark(batchProcessDetail.getAccount().getAccountId());
		transaction.setDebitAccount(batchProcessDetail.getAccount());
		transaction.setCurrency(batchProcessDetail.getAccount().getCurrency());
		
		return transaction;
	}

	@SuppressWarnings("unchecked")
	public List<Account> getAccountsToProcess(BatchProcess batchProcess)
	{
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT a FROM Account a, AccountPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND (coalesce(pt.insurance,0) > 0 OR coalesce(pt.insuranceMortgage,0) > 0) "
				+ "AND a.accountId = pt.account.accountId "
				+ "AND pt.account.accountId NOT IN "
				+ "(SELECT o.account.accountId FROM AccountPortfolio o WHERE o.statusId = '002') "
				+ "AND a.accountStatus.accountStatusId = '002' "
				+ "AND a.product.productType.productClass.productClassId = :productClassId "
				)
				.setParameter("dueDate", batchProcess.getAccountingDate())
				.setParameter("productClassId", ProductClassHelper.LOAN)
				.getResultList();
		
		return accounts;
	}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction, BatchProcessDetail batchProcessDetail) throws Exception
	{
		TransactionAccount ta = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Account account = batchProcessDetail.getAccount();
		
		List<AccountPaytable> accountPaytables = XPersistence.getManager().createQuery("SELECT pt FROM AccountPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND pt.account.accountId = :accountId ")
				.setParameter("accountId", account.getAccountId())
				.setParameter("dueDate", transaction.getAccountingDate())
				.getResultList();
		
		AccountPaytable quota = accountPaytables.get(0);
		
		if (quota.getInsurance()!=null && quota.getInsurance().compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), quota.getInsurance(), transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_RECEIVABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), quota.getInsurance(), transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_PAYABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		if (quota.getInsuranceMortgage()!=null && quota.getInsuranceMortgage().compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), quota.getInsuranceMortgage(), transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_RECEIVABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), quota.getInsuranceMortgage(), transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_PAYABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
		}

		
		return transactionAccounts;
	}
}
