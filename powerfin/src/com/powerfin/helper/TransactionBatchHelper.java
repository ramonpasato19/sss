package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class TransactionBatchHelper {
	
	public final static String TRANSACTION_BATCH_REQUEST_STATUS = "001";
	
	public final static String TRANSACTION_BATCH_COLLECTED_STATUS = "002";
	
	public final static String TRANSACTION_BATCH_IN_PROCESS_STATUS = "003";
	
	public final static String TRANSACTION_BATCH_FINISH_STATUS = "004";
	
	public final static String TRANSACTION_BATCH_DETAIL_CREATE_STATUS = "101";
	public final static String TRANSACTION_BATCH_DETAIL_PROCESS_OK = "102";
	public final static String TRANSACTION_BATCH_DETAIL_PROCESS_ERROR = "103";

	
	
	public static TransactionBatchStatus getTransactionBacthDetailCreateStatus()
	{
		return getTransactionBacthStatus(TRANSACTION_BATCH_DETAIL_CREATE_STATUS);
	}
	
	public static TransactionBatchStatus getTransactionBacthDetailProcessStatus()
	{
		return getTransactionBacthStatus(TRANSACTION_BATCH_DETAIL_PROCESS_OK);
	}
	
	public static TransactionBatchStatus getTransactionBacthDetailProcessErrorStatus()
	{
		return getTransactionBacthStatus(TRANSACTION_BATCH_DETAIL_PROCESS_ERROR);
	}
	
	public static TransactionBatchStatus getTransactionBacthStatus(String transactionBatchStatusId)
	{
		return XPersistence.getManager().find(TransactionBatchStatus.class, transactionBatchStatusId);
	}

	public static TransactionBatchStatus getTransactionBacthRequestStatus()
	{
		return getTransactionBacthStatus(TRANSACTION_BATCH_REQUEST_STATUS);
	}
	
	public static TransactionBatchStatus getTransactionBacthCollectedStatus() {
		return getTransactionBacthStatus(TRANSACTION_BATCH_COLLECTED_STATUS);
	}
	
	public static TransactionBatchStatus getTransactionBacthInProcessStatus() {
		return getTransactionBacthStatus(TRANSACTION_BATCH_IN_PROCESS_STATUS);
	}
	
	public static TransactionBatchStatus getTransactionBacthFinishStatus() {
		return getTransactionBacthStatus(TRANSACTION_BATCH_FINISH_STATUS);
	}
}
