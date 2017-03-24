package com.powerfin.actions.accountInvoice;

import java.util.HashMap;
import java.util.Map;

import com.powerfin.helper.ActionReportHelper;
import com.powerfin.util.report.ReportBaseAction;

public class PrinterInvoice extends ReportBaseAction{
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {
		String accountInvoiceId=getView().getValueString("accountId");
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTINVOICEID", accountInvoiceId);
		return parameters;

	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}

}
