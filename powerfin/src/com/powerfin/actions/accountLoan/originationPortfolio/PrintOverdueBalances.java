package com.powerfin.actions.accountLoan.originationPortfolio;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintOverdueBalances extends ReportBaseAction {

	private String format;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Integer personId = (Integer)getView().getSubview("broker").getValue("personId");
		if (personId==null)
			throw new OperativeException("broker_is_required");
		
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
		
		AccountLoanHelper.getAllOverdueBalancesByBroker(personId, projectedAccountingDate);
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

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
	}
	
	
}