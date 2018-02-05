package com.powerfin.actions.accountLoan.originationPortfolio;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintAccountOverdueBalance extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		
		String accountId = (String)getView().getValue("accountId");
		if (accountId==null)
			throw new OperativeException("account_is_required");
		
		Account a = XPersistence.getManager().find(Account.class, accountId);
		Date accountingDate = (Date)getView().getRoot().getValue("projectedAccountingDate");
		
		if (accountingDate==null)
		{
			accountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("projectedAccountingDate",accountingDate);
		}
		
		Map parameters = new HashMap();
		parameters.put("ACCOUNT_ID", accountId);
		addDefaultParameters(parameters);
		parameters.remove("CURRENT_ACCOUNTING_DATE");
		parameters.put("CURRENT_ACCOUNTING_DATE", accountingDate);
		
		AccountLoanHelper.generateOverdueBalances(a, accountingDate, false);
		XPersistence.getManager().flush();
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