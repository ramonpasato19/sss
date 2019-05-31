package com.powerfin.actions.inventory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openxava.jpa.XPersistence;

import com.powerfin.helper.ActionReportHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.model.Branch;
import com.powerfin.model.Category;
import com.powerfin.util.report.ReportBaseAction;


public class PrintExpiredSpolitItemsAction extends ReportBaseAction {
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {
	Map parameters = new HashMap();
	Date fromDate = (Date)getView().getValue("fromDate");
	Date toDate = (Date)getView().getValue("toDate");
	if (fromDate==null) {
		fromDate = CompanyHelper.getCurrentAccountingDate();			
	}
	if (toDate==null) {
		toDate = CompanyHelper.getCurrentAccountingDate();			
	}
	
	Map<String, Integer> branch = (Map<String, Integer>) getView().getRoot().getValue("branch");
	String parameterBranch="%";
	if (branch.get("branchId")!=null ){
		Branch currentBranch = XPersistence.getManager().find(Branch.class, (Integer)branch.get("branchId"));			
		parameterBranch = currentBranch.getName();			
	}
	
	
	Map<String, String> mapLocation = (Map<String,String>) getView().getRoot().getValue("location");
	if(mapLocation.get("categoryId").isEmpty()) {
		throw new Exception("Debe escoger una Ubicación");
	}
	if (mapLocation.get("categoryId")!=null ){
		Category currentCategory=  XPersistence.getManager().find(Category.class, (String)mapLocation.get("categoryId"));			
		 String location = currentCategory.getCategoryId();
		 parameters.put("UBICACION", location);
	}
	
	
	addDefaultParameters(parameters);
	parameters.put("FROM_DATE", fromDate);	
	parameters.put("TO_DATE", toDate);		
	parameters.put("BRANCH_NAME", parameterBranch);
	
	return parameters;
	
	}
	

	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());		
	}


}
