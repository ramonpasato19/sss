package com.powerfin.actions.transaction;

import java.util.ArrayList;
import java.util.List;

import com.powerfin.helper.CategoryHelper;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;

public class TXUnlockingSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {
		getCreditAccount();
		getValue();
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getCreditAccount();
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(account, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, getValue(), transaction, CategoryHelper.getCategoryById(CategoryHelper.BLOCKED_CATEGORY)));	
		
		return transactionAccounts;
	}
}
