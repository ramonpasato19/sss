package com.powerfin.actions.accountLoan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.powerfin.helper.ActionReportHelper;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintPortfolioRecoveryManagement extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Map parameters = new HashMap();
		Date fromDate = (Date) getView().getRoot().getValue("fromDate");
		Date toDate = (Date) getView().getRoot().getValue("toDate");
		
		if (fromDate==null || toDate==null) {
			throw new Exception("Los campos de Fechas son requeridos");
		}
		if (toDate.before(fromDate)) {
			throw new Exception("Los campos de Fechas son requeridos");
		}
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
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

	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
}