package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoiceSaleSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return AccountInvoiceHelper.getTransactionAccountsForInvoiceSale(transaction);
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountInvoiceHelper.postInvoiceSaleSaveAction(transaction);
	}

}
