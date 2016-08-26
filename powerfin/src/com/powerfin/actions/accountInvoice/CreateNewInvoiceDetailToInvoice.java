package com.powerfin.actions.accountInvoice;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class CreateNewInvoiceDetailToInvoice extends
		CreateNewElementInCollectionAction {

	public void execute() throws Exception {
		
		String accountId = (String) getView().getValue("accountId");

		if (accountId==null || accountId.isEmpty())
			throw new OperativeException("account_invoice_most_be_saved");
		
		super.execute();
	}
}
