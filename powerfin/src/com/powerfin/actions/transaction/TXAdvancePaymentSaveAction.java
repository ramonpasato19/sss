package com.powerfin.actions.transaction;

import java.util.ArrayList;
import java.util.List;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.util.UtilApp;

public class TXAdvancePaymentSaveAction extends TXSaveAction{

	public void extraValidations() throws Exception {
		getDebitAccount();
		getValue();
		if (UtilApp.fieldIsEmpty(getSecondaryCategory()))
			throw new OperativeException("advance_type_is_required");
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, getValue(), transaction, transaction.getSecondaryCategory()));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
}
