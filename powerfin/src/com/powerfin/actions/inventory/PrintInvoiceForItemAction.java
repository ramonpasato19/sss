package com.powerfin.actions.inventory;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintInvoiceForItemAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		String accountId=(String)getView().getValue("account.accountId");
		if(accountId==null)
			throw new OperativeException("account_is_required");
		
		Map parameters = new HashMap();

		addDefaultParameters(parameters);
		parameters.put("ACCOUNT_ID", accountId);
		return parameters;

	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}

}
