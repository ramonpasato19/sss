package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.persistence.Query;

import org.openxava.jpa.XPersistence;

import com.powerfin.helper.*;
import com.powerfin.model.Branch;
import com.powerfin.model.KardexAccountTemp;
import com.powerfin.util.report.*;

public class PrintInventoryAccountItemAction extends ReportBaseAction {
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		Boolean all=(Boolean)getView().getValue("all");
		String accountId=(String)getView().getValue("account.accountId");
		if (fromDate==null) {
			fromDate = CompanyHelper.getCurrentAccountingDate();			
		}

		if (toDate==null) {
			toDate = CompanyHelper.getCurrentAccountingDate();			
		}
		if (accountId==null || accountId.isEmpty()) {
			throw new Exception("Necesita Seleecionar el Producto");
		}

		
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
		parameters.put("ACCOUNT_ITEM_ID", accountId);
		parameters.put("BRANCH_NAME", parameterBranch);
		
		return parameters;

	}
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
	
	
}
