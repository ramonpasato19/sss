package com.powerfin.actions.accountPayable;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintPayableBalances extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		String productId = (String)getView().getSubview("product").getValue("productId");
		if (productId==null)
			throw new OperativeException("product_is_required");
		
		Map parameters = new HashMap();
		parameters.put("PRODUCT_ID", productId);
		addDefaultParameters(parameters);
		
		Date projectedAccountingDate = (Date)getView().getRoot().getValue("projectedAccountingDate");
		if (projectedAccountingDate==null)
		{
			projectedAccountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("projectedAccountingDate",projectedAccountingDate);
		}
		
		parameters.remove("CURRENT_ACCOUNTING_DATE");
		parameters.put("CURRENT_ACCOUNTING_DATE", projectedAccountingDate);

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