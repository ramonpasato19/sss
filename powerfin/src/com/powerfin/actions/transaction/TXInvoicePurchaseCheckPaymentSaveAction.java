package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoicePurchaseCheckPaymentSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		Account creditAccount = getCreditAccount();
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, getValue(), transaction, CategoryHelper.getCheckCategory()));	
		
		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			if(AccountBankHelper.validateAndEmitCheck(transaction.getCreditAccount(), transaction.getDocumentNumber(), getRemark()))
				addMessage("check_emitted");
			
			if (AccountInvoiceHelper.cancelInvoice(transaction.getDebitAccount()))
				addMessage("invoice_cancelled");
			
		}
	}
}
