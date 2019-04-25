package com.powerfin.actions.accountTerm;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintList extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {
		
		String productId = (String)getView().getSubview("product").getValue("productId");
		Date fromDate = (Date)getView().getRoot().getValue("fromDate");
		Date toDate = (Date)getView().getRoot().getValue("toDate");
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("fromDate",fromDate);
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("toDate",toDate);
		
		if (productId==null)
			throw new OperativeException("product_is_required");
		
		Map parameters = new HashMap();
		parameters.put("PRODUCT_ID", productId);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
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