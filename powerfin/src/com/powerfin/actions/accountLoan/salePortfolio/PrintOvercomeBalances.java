package com.powerfin.actions.accountLoan.salePortfolio;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.powerfin.helper.ActionReportHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintOvercomeBalances extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Integer personId = (Integer)getView().getSubview("broker").getValue("personId");
		
		Map parameters = new HashMap();
		parameters.put("BROKER_PERSON_ID", personId);
		addDefaultParameters(parameters);
		
		Date projectedAccountingDate = (Date)getView().getRoot().getValue("projectedAccountingDate");
		if (projectedAccountingDate==null)
		{
			projectedAccountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("projectedAccountingDate",projectedAccountingDate);
		}
		
		parameters.remove("CURRENT_ACCOUNTING_DATE");
		parameters.put("CURRENT_ACCOUNTING_DATE", projectedAccountingDate);
				
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
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
}