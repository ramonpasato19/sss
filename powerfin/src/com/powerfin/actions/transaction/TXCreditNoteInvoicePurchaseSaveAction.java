package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXCreditNoteInvoicePurchaseSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return AccountInvoiceHelper.getTAForCreditNoteInvoicePurchase(transaction);
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountInvoiceHelper.postCreditNoteInvoicePurchaseSaveAction(transaction);
	}

}
