package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.model.*;

public class TXGeneralSaveAction extends TXSaveAction{
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return transaction.getTransactionAccounts();
	}
}
