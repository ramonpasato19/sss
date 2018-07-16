package com.powerfin.actions.accountLoan;

import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintAccountLoanDocuments extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		
		String accountId = (String)getView().getValue("accountId");
		if (accountId==null)
			throw new OperativeException("account_is_required");
				
		Map parameters = new HashMap();
		parameters.put("ACCOUNT_ID", accountId);
		addDefaultParameters(parameters);
		
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

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
}