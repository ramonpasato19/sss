package com.powerfin.actions.balanceAccounting;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintProfitLossBalanceAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date accountingDate = (Date)getView().getValue("accountingDate");
		Integer level = (Integer)getView().getValue("level");
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();
		
		if (level==null)
			level=999;
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTING_DATE", accountingDate);
		parameters.put("LEVEL", level);
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
