package com.powerfin.actions.batch;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class BatchSaveAction extends ViewBaseAction  {

	private String subAction;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
	
		Integer batchProcessId = (Integer)getView().getValue("batchProcessId");
		BatchProcess batchProcess = XPersistence.getManager().find(BatchProcess.class, batchProcessId);
		
		if (batchProcess.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_IN_PROCESS_STATUS))
			throw new OperativeException("batch_process_in_process");
		if (batchProcess.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_FINISH_STATUS))
			throw new OperativeException("batch_process_already_processed");
		
		Class<IBatchSaveAction> actionClass = (Class<IBatchSaveAction>) Class.forName(batchProcess.getBatchProcessType().getActionClass());
		IBatchSaveAction batchSaveActionImp = actionClass.newInstance();
		
		
		if (subAction.equals(BatchProcessHelper.COLLECT_SUBACTION))
		{
			batchProcess.setBatchProcessStatus(BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_COLLECTED_STATUS));
			XPersistence.getManager().merge(batchProcess);
			
			XPersistence.getManager()
			.createQuery("DELETE FROM BatchProcessDetail o "
					+ "WHERE o.batchProcess.batchProcessId = :batchProcessId ")
			.setParameter("batchProcessId", batchProcessId).executeUpdate();
			
			List<Account> accountsToProcess = batchSaveActionImp.getAccountsToProcess(batchProcess);
			
			for (Account account : accountsToProcess)
			{
				BatchProcessDetail bpd = new BatchProcessDetail();
				bpd.setBatchProcess(batchProcess);
				bpd.setAccount(account);
				bpd.setBatchProcessStatus(BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_DETAIL_CREATE_STATUS));
				XPersistence.getManager().persist(bpd);
				XPersistence.commit();
			}
			
			getView().refresh();
			getView().refreshCollections();
			addMessage("complete_collection");
			
		}
		else if (subAction.equals(BatchProcessHelper.PROCESS_SUBACTION)) 
		{
			if (batchProcess.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_REQUEST_STATUS))
				throw new OperativeException("please_collect_information");
			
			batchProcess.setBatchProcessStatus(BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_IN_PROCESS_STATUS));
			XPersistence.getManager().merge(batchProcess);

			List<String> batchDetailstatusToProcess = Arrays.asList(BatchProcessHelper.BATCH_DETAIL_CREATE_STATUS, BatchProcessHelper.BATCH_DETAIL_PROCESS_ERROR);
			Transaction transaction = null;
			List<TransactionAccount> transactionAccounts = null;
			
			List<BatchProcessDetail> batchProcessDetails = XPersistence.getManager().createQuery("SELECT o FROM BatchProcessDetail o "
					+ "WHERE o.batchProcess.batchProcessId = :batchProcessId "
					+ "AND o.batchProcessStatus.batchProcessStatusId IN :batchProcessStatusId")
					.setParameter("batchProcessId", batchProcessId)
					.setParameter("batchProcessStatusId", batchDetailstatusToProcess)
					.getResultList();
			
			for (BatchProcessDetail detailIte : batchProcessDetails)
			{
				transaction = null;
				transactionAccounts = null;
				BatchProcessDetail batchProcessDetail = XPersistence.getManager().find(BatchProcessDetail.class, detailIte.getBatchProcessDetailId());
				try	{
					transaction = batchSaveActionImp.getTransaction(batchProcess, batchProcessDetail); 			
					transactionAccounts = batchSaveActionImp.getTransactionAccounts(transaction, batchProcessDetail);
					XPersistence.getManager().persist(transaction);

		  			TransactionHelper.processTransaction(transaction, transactionAccounts);
		  			
		  			batchProcessDetail.setBatchProcessStatus(
							BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_DETAIL_PROCESS_OK));
					
				} catch (Exception e) {
					batchProcessDetail.setBatchProcessStatus(
							BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_DETAIL_PROCESS_ERROR));
					batchProcessDetail.setErrorMessage(e.getMessage());
					transaction.setTransactionStatus(TransactionHelper.getTransactionStatusByStatusId(TransactionHelper.TRANSACTION_ANNULLED_STATUS_ID));
					XPersistence.getManager().merge(transaction);
				}

				XPersistence.getManager().merge(batchProcessDetail);
				XPersistence.commit();
			}
			
			batchProcess = XPersistence.getManager().find(BatchProcess.class, batchProcessId);
			batchProcess.setBatchProcessStatus(BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_FINISH_STATUS));
			XPersistence.getManager().merge(batchProcess);
			XPersistence.commit();
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
