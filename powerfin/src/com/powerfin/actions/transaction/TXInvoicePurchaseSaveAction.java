package com.powerfin.actions.transaction;

import java.util.List;

import com.powerfin.helper.AccountInvoiceHelper;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;

public class TXInvoicePurchaseSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return AccountInvoiceHelper.getTransactionAccountsForInvoicePurchase(transaction);
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountInvoiceHelper.postInvoicePurchaseSaveAction(transaction);
	}

}
