package com.powerfin.actions.accountRetention;

import java.math.*;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class CreateNewRetentionDetailToRetention extends
		CreateNewElementInCollectionAction {

	public void execute() throws Exception {
		String accountId = (String) getView().getValue("accountId");

		if (accountId==null || accountId.isEmpty())
			throw new OperativeException("account_retention_most_be_saved");
		super.execute(); // Ejecuta la lógica estándar, la cual muestra un
							// diálogo
		BigDecimal subtotal = (BigDecimal) getPreviousView().getSubview(
				"accountInvoice").getValue("subtotal");
		BigDecimal vat = (BigDecimal) getPreviousView().getSubview(
				"accountInvoice").getValue("vat");
		getCollectionElementView().setValue("subtotal", subtotal);
		getCollectionElementView().setValue("vat", vat);
	}
}
