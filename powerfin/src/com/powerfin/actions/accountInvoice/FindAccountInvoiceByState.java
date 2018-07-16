package com.powerfin.actions.accountInvoice;

import java.util.Calendar;
import java.util.Date;

import org.openxava.actions.ViewBaseAction;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.CompanyHelper;
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
		//Purchase, Sale, ElectronicSale, Dispatch, ControlIncome, ControlExpense
		if (type != null && type.equals("Sale"))
			getView().setValue("typeInvoiceSelected", "102");
		else if (type != null && type.equals("Purchase"))
			getView().setValue("typeInvoiceSelected", "202");
		else if (type != null && type.equals("ElectronicSale"))
			getView().setValue("typeInvoiceSelected", "1021");
		else if (type != null && type.equals("Dispatch"))
			getView().setValue("typeInvoiceSelected", "1022");
		else if (type != null && type.equals("ControlIncome"))
			getView().setValue("typeInvoiceSelected", "1023");
		else if (type != null && type.equals("ControlExpense"))
			getView().setValue("typeInvoiceSelected", "2023");
		else 
			throw new OperativeException("type_invoice_is_required");
		getView().refreshCollections();
	}
}
