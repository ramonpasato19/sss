package com.powerfin.actions.balanceAccounting;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintAccountingDailyBookAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		String bookAccountId = getView().getSubview("bookAccount").getValueString("bookAccountId");
		Calendar initialBalanceDate = Calendar.getInstance();
		
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		getView().setValue("fromDate",fromDate);
		getView().setValue("toDate",toDate);
		
		initialBalanceDate.setTime(fromDate);
		initialBalanceDate.add(Calendar.DAY_OF_MONTH, -1);
		
		if (bookAccountId==null || bookAccountId.isEmpty())
			throw new OperativeException("book_account_is_required");
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("BOOK_ACCOUNT", bookAccountId);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("INITIAL_BALANCE_DATE", initialBalanceDate.getTime());
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