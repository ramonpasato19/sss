package com.powerfin.actions.transaction;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class TXGeneralSaveAction extends TXSaveAction{
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return ((Transaction)XPersistence.getManager().find(Transaction.class, transaction.getTransactionId())).getTransactionAccounts();
	}
}
