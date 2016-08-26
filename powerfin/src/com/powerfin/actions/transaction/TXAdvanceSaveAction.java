package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXAdvanceSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account creditAccount = getCreditAccount();
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(creditAccount, getValue(), transaction, transaction.getSecondaryCategory()));
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
}
