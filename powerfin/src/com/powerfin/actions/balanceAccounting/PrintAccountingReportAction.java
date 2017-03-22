package com.powerfin.actions.balanceAccounting;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintAccountingReportAction extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date accountingDate = (Date)getView().getValue("accountingDate");
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();

		getView().getRoot().setValue("accountingDate", accountingDate);
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTING_DATE", accountingDate);
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

	@Override
	protected String getReportName() throws Exception {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
