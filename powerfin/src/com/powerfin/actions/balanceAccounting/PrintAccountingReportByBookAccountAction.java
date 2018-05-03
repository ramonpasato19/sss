package com.powerfin.actions.balanceAccounting;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintAccountingReportByBookAccountAction extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		String bookAccountId = getView().getSubview("bookAccount").getValueString("bookAccountId");
		if (bookAccountId==null)
			throw new OperativeException("book_account_is_required");
		
		Date accountingDate = (Date)getView().getValue("projectedAccountingDate");
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();

		getView().getRoot().setValue("projectedAccountingDate", accountingDate);
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTING_DATE", accountingDate);
		parameters.put("BOOK_ACCOUNT", bookAccountId);
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
