package com.powerfin.actions.account;

import java.util.*;

import net.sf.jasperreports.engine.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintAccountMovementAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		Integer subaccount = (Integer)getView().getValue("subaccount");
		String accountId = getView().getSubview("account").getValueString("accountId");
		
		Map<String, String> mapCategories = (Map<String, String>) getView().getValue("category");
		String categoryId = (String)mapCategories.get("categoryId");
		
		Map<String, Integer> mapBranches = (Map<String, Integer>) getView().getValue("branch");
		Integer branchId = null;
		
		if(mapBranches!=null)
		{
			branchId = (Integer)mapBranches.get("branchId");
		}

		Calendar initialBalanceDate = Calendar.getInstance();
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		if (subaccount==null)
			subaccount = 0;
		
		if (branchId==null)
			branchId = 1;
		
		try
		{
			getView().setValue("fromDate",fromDate);
			getView().setValue("toDate",toDate);
			getView().setValue("subaccount",subaccount);
		}catch (Exception e)
		{		
		}
		
		initialBalanceDate.setTime(fromDate);
		initialBalanceDate.add(Calendar.DAY_OF_MONTH, -1);
		
		if (accountId==null || accountId.isEmpty())
			throw new OperativeException("account_is_required");
		
		if (categoryId==null || categoryId.isEmpty())
			throw new OperativeException("category_is_required");
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNT", accountId);
		parameters.put("CATEGORY", categoryId);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("INITIAL_BALANCE_DATE", initialBalanceDate.getTime());
		parameters.put("BRANCH", branchId);
		parameters.put("SUBACCOUNT", subaccount);
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
