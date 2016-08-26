package com.powerfin.actions.transaction;

import java.util.*;

import org.openxava.model.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXGeneralSaveAction extends TXSaveAction{

	@SuppressWarnings("rawtypes")
	public void executeOld() throws Exception
	{
		// Save Transaction
		super.execute();
		
		if (getErrors().isEmpty()) { 
			
			// Find the just saved transaction
            Map keyValues = getView().getKeyValues(); 
            Transaction transaction = (Transaction) MapFacade.findEntity(getView().getModelName(), keyValues); 
            boolean financialProcessed = false;
            // Process transaction and Financial
            financialProcessed = TransactionHelper.processTransaction(transaction);
			if (financialProcessed)
				addMessage("financial_created");
			
			//Clear all fields
			getView().clear();
		}
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return null;
	}
}
