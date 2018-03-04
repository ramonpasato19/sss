package com.powerfin.actions.transaction;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoiceSalePaymentSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {
		super.extraValidations();
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{		
			if (AccountInvoiceHelper.cancelInvoice(transaction.getCreditAccount()))
				addMessage("invoice_cancelled");
		}
	}
}
