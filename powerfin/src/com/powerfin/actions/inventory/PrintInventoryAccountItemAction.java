package com.powerfin.actions.inventory;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintInventoryAccountItemAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		Boolean all=(Boolean)getView().getValue("all");
		String accountId=(String)getView().getValue("account.accountId");
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();

		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();


		Map parameters = new HashMap();

		addDefaultParameters(parameters);
		
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("ALL", all);
		parameters.put("ACCOUNT_ID", accountId);
		return parameters;

	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}

}
