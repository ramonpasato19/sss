package com.powerfin.actions.negotiation;

import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;


public class PrintOriginationNegotiationAction extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Integer negotiationId = (Integer)getView().getValue("negotiationId");
		if (negotiationId==null)
			throw new OperativeException("negotiation_id_is_required");

		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("NEGOTIATION_ID", negotiationId);
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
