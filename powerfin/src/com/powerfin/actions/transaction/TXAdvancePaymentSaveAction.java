package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXAdvancePaymentSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, getValue(), transaction, transaction.getSecondaryCategory()));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
}
