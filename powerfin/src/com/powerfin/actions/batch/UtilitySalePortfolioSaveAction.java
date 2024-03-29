package com.powerfin.actions.batch;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class UtilitySalePortfolioSaveAction implements IBatchSaveAction  {

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
		//Obtiene las cuotas de operaciones activas y que no sean operaciones vendidas
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT a FROM Account a, AccountPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND coalesce(pt.utilitySalePortfolio, 0) > 0 "
				+ "AND a.accountId = pt.account.accountId "
				+ "AND pt.account.accountId IN "
				+ "(SELECT o.account.accountId FROM AccountPortfolio o "
				+ "WHERE o.accountPortfolioStatus.accountPortfolioStatusId = :accountPortfolioStatusId "
				+ "AND o.saleStatus.accountStatusId = :accountStatusId) "
				+ "AND a.product.productType.productClass.productClassId = :productClassId "
				)
				.setParameter("dueDate", batchProcess.getAccountingDate())
				.setParameter("accountStatusId", AccountLoanHelper.STATUS_LOAN_ACTIVE)
				.setParameter("productClassId", ProductClassHelper.LOAN)
				.setParameter("accountPortfolioStatusId", AccountLoanHelper.SALE_PORTFOLIO_STATUS_ID)
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
		
		for (AccountPaytable quota : accountPaytables)
		{
			if (quota.getUtilitySalePortfolio() != null && quota.getUtilitySalePortfolio().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, 0, quota.getUtilitySalePortfolio(), transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_PR_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, 0, quota.getUtilitySalePortfolio(), transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_IN_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
		}
		
		return transactionAccounts;
	}
}
