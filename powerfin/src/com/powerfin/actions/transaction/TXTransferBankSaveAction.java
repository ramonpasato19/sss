package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXTransferBankSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		Account creditAccount = getCreditAccount();

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(creditAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
}
