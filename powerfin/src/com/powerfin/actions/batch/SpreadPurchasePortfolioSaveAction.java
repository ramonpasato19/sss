package com.powerfin.actions.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openxava.jpa.XPersistence;
import org.openxava.util.XavaResources;

import com.powerfin.helper.AccountLoanHelper;
import com.powerfin.helper.CategoryHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.helper.ProductClassHelper;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.helper.TransactionHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountPaytable;
import com.powerfin.model.BatchProcess;
import com.powerfin.model.BatchProcessDetail;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;

public class SpreadPurchasePortfolioSaveAction implements IBatchSaveAction  {

	public SpreadPurchasePortfolioSaveAction()
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
		//Obtiene las cuotas de operaciones activas ya sea vendidas o no vendidas, cuyo saldo de spread sea mayor a 0
		List<Account> accounts = XPersistence.getManager()
				.createQuery("SELECT a FROM Account a, AccountPaytable pt, Balance b "
				+ "WHERE pt.dueDate = :dueDate "
				+ "AND coalesce(pt.purchaseSpread, 0) > 0 "
				+ "AND a.accountId = pt.account.accountId "
				+ "AND a.accountStatus.accountStatusId = :accountStatusId "
				+ "AND a.product.productType.productClass.productClassId = :productClassId "
				+ "AND b.account.accountId = a.accountId "
				+ "AND :accountingDate BETWEEN b.fromDate AND b.toDate "
				+ "AND b.category.categoryId = :categoryPuchaseSpread "
				+ "AND b.branch.branchId = a.branch.branchId "
				+ "AND coalesce(b.balance, 0) > 0 ")
				.setParameter("dueDate", batchProcess.getAccountingDate())
				.setParameter("accountStatusId", AccountLoanHelper.STATUS_LOAN_ACTIVE)
				.setParameter("productClassId", ProductClassHelper.LOAN)
				.setParameter("categoryPuchaseSpread", CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY)
				.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
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
			if (quota.getPurchaseSpread() != null && quota.getPurchaseSpread().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, 0, quota.getPurchaseSpread(), transaction, CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, 0, quota.getPurchaseSpread(), transaction, CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_EX_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
		}
		return transactionAccounts;
	}
}
