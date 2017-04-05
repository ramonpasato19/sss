package com.powerfin.actions.accountInvoice;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.helper.*;
public class FindAccountInvoiceByState extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Date fromDate = (Date) getView().getValue("fromDate");
		Date toDate = (Date) getView().getValue("toDate");
		String type = null;
		if (getView().getValue("typeInvoice") != null)
			type = getView().getValue("typeInvoice").toString();
		if (fromDate == null) {
			fromDate = CompanyHelper.getCurrentAccountingDate();
			Calendar cFromDate = Calendar.getInstance();
			cFromDate.setTime(fromDate);
			cFromDate.set(Calendar.DAY_OF_MONTH, 1);
			fromDate = cFromDate.getTime();
		}
		if (toDate == null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		getView().setValue("fromDate", fromDate);
		getView().setValue("toDate", toDate);
		if (type != null && type == "Sale")
			getView().setValue("typeInvoiceSelected", AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID);
		else
			getView().setValue("typeInvoiceSelected", AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID);
		getView().refreshCollections();
	}
}
