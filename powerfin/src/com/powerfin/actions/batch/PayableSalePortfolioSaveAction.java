package com.powerfin.actions.batch;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class PayableSalePortfolioSaveAction implements IBatchSaveAction  {

	public PayableSalePortfolioSaveAction()
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
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT a FROM Account a, AccountSoldPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND a.accountId = pt.account.accountId "
				+ "AND pt.account.accountId IN "
				+ "(SELECT o.account.accountId FROM AccountPortfolio o WHERE o.statusId = '002' AND o.saleStatus = '002') "
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
		
		List<AccountSoldPaytable> accountSoldPaytables = XPersistence.getManager().createQuery(
				"SELECT pt FROM AccountSoldPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND pt.account.accountId = :accountId ")
				.setParameter("accountId", account.getAccountId())
				.setParameter("dueDate", transaction.getAccountingDate())
				.getResultList();
		
		for (AccountSoldPaytable quota : accountSoldPaytables)
		{
			if (quota.getInterest()!=null && quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), quota.getInterest(), transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("interest_quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, 0, quota.getInterest(), transaction, CategoryHelper.getCategoryById(CategoryHelper.INTDIF_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("interest_quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
			
			if (quota.getCapital()!=null && quota.getCapital().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.SALE_CAPITAL_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("capital_quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, 0, quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPDIF_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("capital_quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
		}
		return transactionAccounts;
	}
}
