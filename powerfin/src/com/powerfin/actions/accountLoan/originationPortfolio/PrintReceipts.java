package com.powerfin.actions.accountLoan.originationPortfolio;

import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.util.report.*;

import net.sf.jasperreports.engine.*;

public class PrintReceipts extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		String productId = (String)getView().getSubview("product").getValue("productId");
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");	
		
		if (fromDate==null)
			throw new OperativeException("from_date_is_required");
		
		if (toDate==null)
			throw new OperativeException("to_date_is_required");
		
		if (productId==null)
			throw new OperativeException("product_is_required");
		
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("PRODUCT_ID", productId);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		
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