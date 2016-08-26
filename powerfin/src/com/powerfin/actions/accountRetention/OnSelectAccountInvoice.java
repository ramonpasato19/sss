package com.powerfin.actions.accountRetention;

import org.openxava.actions.*;

public class OnSelectAccountInvoice extends OnChangePropertyBaseAction {

	@Override
	public void execute() throws Exception {
		if (getNewValue() == null)
			return;
		getView().setValue("accountInvoiceId", getNewValue());
		addMessage("account_invoice_setted");
	}
}
