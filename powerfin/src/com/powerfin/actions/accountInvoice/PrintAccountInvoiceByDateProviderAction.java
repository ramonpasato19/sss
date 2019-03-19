package com.powerfin.actions.accountInvoice;

import java.util.*;

import org.openxava.jpa.XPersistence;

import net.sf.jasperreports.engine.*;

import com.powerfin.helper.*;
import com.powerfin.model.Branch;
import com.powerfin.util.report.*;

public class PrintAccountInvoiceByDateProviderAction extends ReportBaseAction {
private String type;	
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");	
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		getView().setValue("fromDate",fromDate);
		getView().setValue("toDate",toDate);
		
		Map<String, Integer> branch = (Map<String, Integer>) getView().getRoot().getValue("branch");
		String parameterBranch="%";
		if (branch.get("branchId")!=null ){
			Branch currentBranch=  XPersistence.getManager().find(Branch.class, (Integer)branch.get("branchId"));			
			parameterBranch = currentBranch.getName();			
		}
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("SUCURSAL", parameterBranch);
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
		if (type.equals("purchase"))
			return ActionReportHelper.getReportByAction(this.getClass().getName()+"PURCHASE");
		else
			return ActionReportHelper.getReportByAction(this.getClass().getName()+"SALE");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
