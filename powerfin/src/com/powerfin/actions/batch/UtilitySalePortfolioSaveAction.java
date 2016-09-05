package com.powerfin.actions.batch;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class UtilitySalePortfolioSaveAction extends ViewBaseAction  {

	private String subAction;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
	
		Integer batchProcessId = (Integer)getView().getValue("batchProcessId");
		Date accountingDate = (Date)getView().getValue("accountingDate");
		
		if (subAction.equals("COLLECT"))
		{
			
			List<AccountPaytable> accountPaytables = XPersistence.getManager().createQuery("SELECT pt FROM AccountPaytable pt "
					+ "WHERE pt.dueDate = :dueDate "
					+ "AND coalesce(pt.utilitySalePortfolio, 0) > 0 "
					+ "AND pt.account.accountId IN "
					+ "(SELECT o.account.accountId FROM AccountPortfolio o WHERE o.statusId = '002') "
					+ "AND pt.account.accountId IN "
					+ "(SELECT o.accountId FROM Account o WHERE o.accountStatus.accountStatusId = '002') "
					)
					.setParameter("dueDate", accountingDate)
					.getResultList();
			
			BatchProcess bp = XPersistence.getManager().find(BatchProcess.class, batchProcessId);
			int count = 1;
			for (AccountPaytable pt : accountPaytables)
			{
				BatchProcessDetail bpd = new BatchProcessDetail();
				bpd.setBatchProcess(bp);
				bpd.setAccount(pt.getAccount());
				bpd.setBatchProcessStatus(bp.getBatchProcessStatus());
				XPersistence.getManager().persist(bpd);
				//if (count == 100) XPersistence.getManager().flush();
				count++;
			}
			getView().refreshCollections();
			addMessage("complete_collection");
		}else
		{
			TransactionStatus transactionStatus =XPersistence.getManager().find(TransactionStatus.class, "002"); 
			BatchProcessStatus processStatus = XPersistence.getManager().find(BatchProcessStatus.class, "002");
			BatchProcessStatus processStatusError = XPersistence.getManager().find(BatchProcessStatus.class, "003");
			BatchProcessStatus completeStatus = XPersistence.getManager().find(BatchProcessStatus.class, "004");
			List<String> statusToProcess = Arrays.asList("001", "003");
			
			List<BatchProcessDetail> batchProcessDetails = XPersistence.getManager().createQuery("SELECT o FROM BatchProcessDetail o "
					+ "WHERE o.batchProcess.batchProcessId = :batchProcessId "
					+ "AND o.batchProcessStatus.batchProcessStatusId IN :batchProcessStatusId")
					.setParameter("batchProcessId", batchProcessId)
					.setParameter("batchProcessStatusId", statusToProcess)
					.getResultList();
			//TODO
			int count = 1;
			TransactionAccount ta = null;
			TransactionModule transactionModule = XPersistence.getManager().find(TransactionModule.class, BatchProcessHelper.UTILITY_SALE_PORTFOLIO_TRANSACTION_MODULE);
			boolean financialProcessed = false;
			String errorMessage = null;
			
			for (BatchProcessDetail detail : batchProcessDetails)
			{
				financialProcessed = false;
				errorMessage = null;
				Account account = detail.getAccount();
				List<AccountPaytable> accountPaytables = XPersistence.getManager().createQuery("SELECT pt FROM AccountPaytable pt "
						+ "WHERE pt.dueDate = :dueDate "
						+ "AND pt.account.accountId = :accountId ")
						.setParameter("accountId", account.getAccountId())
						.setParameter("dueDate", accountingDate)
						.getResultList();
				
				AccountPaytable quota = accountPaytables.get(0);
				
				Transaction transaction = TransactionHelper.getNewInitTransaction();
	  			transaction.setTransactionModule(transactionModule);
	  			transaction.setTransactionStatus(transactionModule.getDefaultTransactionStatus());
	  			transaction.setValue(quota.getUtilitySalePortfolio());
	  			transaction.setRemark(account.getAccountId());
	  			transaction.setDebitAccount(account);
	  			transaction.setCurrency(account.getCurrency());
	  			
	  			XPersistence.getManager().persist(transaction);
	  			
				List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, 0, quota.getUtilitySalePortfolio(), transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_PR_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				XPersistence.getManager().persist(ta);
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, 0, quota.getUtilitySalePortfolio(), transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_IN_CATEGORY));
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				XPersistence.getManager().persist(ta);
				transactionAccounts.add(ta);
				
				transaction.setTransactionAccounts(transactionAccounts);
				transaction.setTransactionStatus(transactionStatus);
				
				
				try{
					financialProcessed = TransactionHelper.processTransaction(transaction);
				}catch (Exception e)
				{
					errorMessage = e.getMessage();
					e.printStackTrace();
				}
				
				if (financialProcessed)
				{
					detail.setBatchProcessStatus(processStatus);
					detail.setErrorMessage("OK");
				}
				else
				{
					detail.setBatchProcessStatus(processStatusError);
					detail.setErrorMessage(errorMessage);
					for (TransactionAccount  transactionAccount: transactionAccounts)
						XPersistence.getManager().remove(transactionAccount);
					XPersistence.getManager().remove(transaction);
				}
				
				XPersistence.getManager().merge(detail);
				
				if (count == 100) 
				{
					getView().refreshCollections();
					count = 0;
				}
				count++;
			}
			BatchProcess bp = XPersistence.getManager().find(BatchProcess.class, batchProcessId);
			bp.setBatchProcessStatus(completeStatus);
			XPersistence.getManager().merge(bp);
			getView().refreshCollections();
			getView().refresh();
			addMessage("complete_process");
		}
	}

	public String getSubAction() {
		return subAction;
	}

	public void setSubAction(String subAction) {
		this.subAction = subAction;
	}
}
