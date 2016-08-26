package com.powerfin.actions.negotiation;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;


public class PrintSaleNegotiationAction extends ReportBaseAction {

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

	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
}
