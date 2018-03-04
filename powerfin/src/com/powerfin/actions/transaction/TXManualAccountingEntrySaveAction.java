package com.powerfin.actions.transaction;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class TXManualAccountingEntrySaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {
		
	}

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return ((Transaction)XPersistence.getManager().find(Transaction.class, transaction.getTransactionId())).getTransactionAccounts();
	}
}
