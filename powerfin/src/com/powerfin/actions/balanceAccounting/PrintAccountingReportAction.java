package com.powerfin.actions.balanceAccounting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.model.types.Types;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintAccountingReportAction extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date accountingDate = (Date)getView().getValue("accountingDate");
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		Integer level = (Integer)getView().getValue("level");
		
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();

		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();
		
		if (level==null)
			level = 100;
		
		getView().getRoot().setValue("accountingDate", accountingDate);
		getView().getRoot().setValue("fromDate", fromDate);
		getView().getRoot().setValue("toDate", toDate);
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTING_DATE", accountingDate);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("LEVEL", level);
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

	public String getFormat() {
		Types.ReportFormat reportFormat = (Types.ReportFormat)getView().getValue("reportFormat");
		return reportFormat.toString();
	}

	public void validate()
	{
		Types.ReportFormat format = (Types.ReportFormat)getView().getValue("reportFormat");
		if (format==null)
			throw new OperativeException("format_is_required");
	}
}
