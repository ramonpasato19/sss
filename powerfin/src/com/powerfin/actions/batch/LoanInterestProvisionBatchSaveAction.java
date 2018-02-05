package com.powerfin.actions.batch;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class LoanInterestProvisionBatchSaveAction implements IBatchSaveAction  {

	public LoanInterestProvisionBatchSaveAction()
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
		//Obtiene las cuotas de operaciones activas y que no sean operaciones vendidas
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT a FROM Account a, AccountPaytable pt "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND coalesce(pt.interest,0) > 0 "
				+ "AND a.accountId = pt.account.accountId "
				+ "AND a.accountStatus.accountStatusId = :accountStatusId "
				+ "AND a.product.productType.productClass.productClassId = :productClassId "
				+ "AND pt.account.accountId NOT IN "
				+ "(SELECT o.account.accountId FROM AccountPortfolio o WHERE o.accountPortfolioStatus.accountPortfolioStatusId = :accountPortfolioStatusId) "
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
		BigDecimal provision = null;
		List<AccountPaytable> accountPaytables = XPersistence.getManager().createQuery("SELECT pt FROM AccountPaytable pt "
				+ "WHERE :accountingDate BETWEEN pt.dueDate-(pt.provisionDays-1) AND pt.dueDate "
				+ "AND pt.account.accountId = :accountId ")
				.setParameter("accountId", account.getAccountId())
				.setParameter("accountingDate", transaction.getAccountingDate())
				.getResultList();
		
		for(AccountPaytable quota : accountPaytables)
		{
			provision = AccountHelper.getDailyProvision(quota, batchProcessDetail.getBatchProcess().getAccountingDate());
			
			if (provision!=null && provision.compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), provision, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_EX_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), provision, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_PR_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
		}
		
		return transactionAccounts;
	}
}
