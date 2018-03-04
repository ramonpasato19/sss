package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.UtilApp;

public class TXThirdAdvancePaymentSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {
		super.extraValidations();
		if (UtilApp.fieldIsEmpty(getSecondaryCategory()))
			throw new OperativeException("advance_type_is_required");
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		Account creditAccount = getCreditAccount();

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, getValue(), transaction, transaction.getSecondaryCategory()));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
}
