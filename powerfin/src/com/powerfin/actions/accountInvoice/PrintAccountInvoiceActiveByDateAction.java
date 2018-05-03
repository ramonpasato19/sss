package com.powerfin.actions.accountInvoice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.AccountInvoiceHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintAccountInvoiceActiveByDateAction extends ReportBaseAction {

	private String type;
	private String reportName;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Date fromDate = (Date) getView().getValue("fromDate");
		Date toDate = (Date) getView().getValue("toDate");

		if (fromDate == null)
			fromDate = CompanyHelper.getCurrentAccountingDate();

		if (toDate == null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		getView().setValue("fromDate", fromDate);
		getView().setValue("toDate", toDate);

		String type = null;
		if (getView().getValue("typeInvoice") != null)
			type = getView().getValue("typeInvoice").toString();
		else
			throw new OperativeException("select_the_type_product_invoice");

		Map parameters = new HashMap();

		addDefaultParameters(parameters);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		if (type != null && type == "Sale") {
			parameters.put("TYPE_INVOICE", AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID);
			getView().setValue("typeInvoiceSelected", AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID);
		} else {
			parameters.put("TYPE_INVOICE", AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID);
			getView().setValue("typeInvoiceSelected", AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID);
		}

		return parameters;
	}

	@Override
	protected JRDataSource getDataSource() throws Exception {
		return null;
	}

	@Override
	protected String getJRXML() throws Exception {
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}