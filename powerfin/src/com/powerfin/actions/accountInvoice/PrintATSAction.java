package com.powerfin.actions.accountInvoice;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.helper.*;
import com.powerfin.util.*;
import com.powerfin.util.report.*;

public class PrintATSAction extends ReportBaseAction {

	private String type;	
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");	
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		String sFromDate = UtilApp.dateToString(fromDate);
		String sToDate = UtilApp.dateToString(toDate);
		getView().setValue("fromDate",fromDate);
		getView().setValue("toDate",toDate);
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("FROM_DATE", sFromDate);
		parameters.put("TO_DATE", sToDate);
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
		return "ATS";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
