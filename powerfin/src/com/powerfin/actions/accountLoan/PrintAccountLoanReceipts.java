package com.powerfin.actions.accountLoan;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintAccountLoanReceipts extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		
		String accountId = (String)getView().getValue("accountId");
		if (accountId==null)
			throw new OperativeException("account_is_required");
		
		Map parameters = new HashMap();
		parameters.put("ACCOUNT_ID", accountId);
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