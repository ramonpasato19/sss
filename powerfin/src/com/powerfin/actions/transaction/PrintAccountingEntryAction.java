package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintAccountingEntryAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		String transactionId = (String)getView().getValue("transactionId");
		if (transactionId==null)
			throw new InternalException("transaction_id_is_required");

		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("TRANSACTION_ID", transactionId);
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
		return "ACCOUNTINGENTRY";
	}

}