package com.powerfin.actions.transaction;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintDocumentTransfer extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		String voucherId=(String)getView().getValue("voucher");
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("VOUCHER", voucherId);
		return parameters;

	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
}
