package com.powerfin.actions.accountLoan.purchasePortfolio;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintReceipts extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Integer personId = (Integer)getView().getSubview("broker").getValue("personId");
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		
		if (fromDate==null)
			throw new OperativeException("from_date_is_required");
		
		if (toDate==null)
			throw new OperativeException("to_date_is_required");
		
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("BROKER_PERSON_ID", personId);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		
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