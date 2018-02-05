package com.powerfin.actions.person;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintActiveLoans extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Integer personId = (Integer)getView().getValue("personId");
		if (personId==null)
			throw new OperativeException("person_is_required");
		
		Map parameters = new HashMap();
		parameters.put("PERSON_ID", personId);
		addDefaultParameters(parameters);
		
		Date projectedAccountingDate = (Date)getView().getValue("projectedAccountingDate");
		if (projectedAccountingDate==null)
		{
			projectedAccountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("projectedAccountingDate",projectedAccountingDate);
		}
		
		parameters.remove("CURRENT_ACCOUNTING_DATE");
		parameters.put("CURRENT_ACCOUNTING_DATE", projectedAccountingDate);
		
		AccountLoanHelper.getAllOverdueBalancesByPerson(personId, projectedAccountingDate);
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