package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoicePurchaseSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return AccountInvoiceHelper.getTransactionAccountsForInvoicePurchase(transaction);
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountInvoiceHelper.postInvoicePurchaseSaveAction(transaction);
	}

}
