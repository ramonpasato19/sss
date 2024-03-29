package com.powerfin.actions.inventory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openxava.jpa.XPersistence;

import com.powerfin.helper.ActionReportHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.model.Branch;
import com.powerfin.util.report.ReportBaseAction;

public class PrintExpiredItemsAction extends ReportBaseAction {
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		if (fromDate==null) {
			fromDate = CompanyHelper.getCurrentAccountingDate();			
		}
		if (toDate==null) {
			toDate = CompanyHelper.getCurrentAccountingDate();			
		}
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		return parameters;
	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
	
}
