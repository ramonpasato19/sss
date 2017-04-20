package com.powerfin.actions.batch;

import java.util.*;

import com.powerfin.model.*;

public interface IBatchSaveAction {
		
	public Transaction getTransaction(BatchProcess batchProcess, BatchProcessDetail batchProcessDetail) throws Exception;

	public List<Account> getAccountsToProcess(BatchProcess batchProcess) throws Exception;
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction, BatchProcessDetail batchProcessDetail) throws Exception;

}
