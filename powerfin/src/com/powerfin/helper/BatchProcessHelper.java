package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class BatchProcessHelper {

	public final static String COLLECT_SUBACTION = "COLLECT";
	public final static String PROCESS_SUBACTION = "PROCESS";
	
	public final static String BATCH_REQUEST_STATUS = "001";
	
	public final static String BATCH_COLLECTED_STATUS = "002";
	
	public final static String BATCH_IN_PROCESS_STATUS = "003";
	
	public final static String BATCH_FINISH_STATUS = "004";
	
	public final static String BATCH_DETAIL_CREATE_STATUS = "101";
	public final static String BATCH_DETAIL_PROCESS_OK = "102";
	public final static String BATCH_DETAIL_PROCESS_ERROR = "103";
	
	public final static String TERM_INTEREST_TRANSACTION_MODULE = "BATCHTERMINTEREST";
	
	
	public final static String LOAN_INTEREST_TRANSACTION_MODULE = "BATCHLOANINTEREST";
	public final static String RECEIVABLE_SALE_PORTFOLIO_TRANSACTION_MODULE = "BATCHRECEIVABLESALEPORTFOLIO";
	public final static String PAYABLE_SALE_PORTFOLIO_TRANSACTION_MODULE = "BATCHPAYABLESALEPORTFOLIO";
	public final static String UTILITY_SALE_PORTFOLIO_TRANSACTION_MODULE = "BATCHUTILITYSALEPORTFOLIO";
	public final static String SPREAD_PURCHASE_PORTFOLIO_TRANSACTION_MODULE = "BATCHSPREADPURCHASEPORTFOLIO";
	
	public static BatchProcessStatus getBatchProcessStatus(String batchPorcessStatusId)
	{
		return XPersistence.getManager().find(BatchProcessStatus.class, batchPorcessStatusId);
	}
	
	
}
