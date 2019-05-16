package com.powerfin.actions.report;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.model.AccountItemType;
import com.powerfin.model.Branch;
import com.powerfin.model.types.Types;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintSalesReportAction extends ReportBaseAction {

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Date accountingDate = (Date)getView().getValue("accountingDate");
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		Integer level = (Integer)getView().getValue("level");
		
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();

		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();
				
		Map<String, Integer> mapBranch = (Map<String, Integer>) getView().getRoot().getValue("branch");
		String branch="%";
		if (mapBranch.get("branchId")!=null ){
			Branch currentBranch=  XPersistence.getManager().find(Branch.class, (Integer)mapBranch.get("branchId"));			
			branch = currentBranch.getName();
		}
		
		getView().getRoot().setValue("accountingDate", accountingDate);
		getView().getRoot().setValue("fromDate", fromDate);
		getView().getRoot().setValue("toDate", toDate);
		
		Map parameters = new HashMap();
		
		addDefaultParameters(parameters);
		parameters.put("ACCOUNTING_DATE", accountingDate);
		parameters.put("FROM_DATE", fromDate);
		parameters.put("TO_DATE", toDate);
		parameters.put("SUCURSAL",branch );
		parameters.put("LEVEL", level);
		
		
		if (reportName.equals("EVOLUTION_CATEGORY")|| reportName.equals("SALES_TRANSACTIONS_DAILY_PER_HOUR")|| reportName.equals("SALES_TRANSACTIONS_MONTHLY_PER_HOUR")) {
			if (mapBranch.get("branchId")==null) {
				throw new Exception("Debe escoger una sucursal");
			}
		}
		if (reportName.equals("EVOLUTION_CATEGORY_ACCUMULATED")) {
			String branchAux="%";
			parameters.put("SUCURSAL",branchAux );
		}
		if (reportName.equals("DAILY_EVOLUTION_CATEGORY")) {
			if (mapBranch.get("branchId")==null) {
				throw new Exception("Debe escoger una sucursal");
			}
		}
		if (reportName.equals("DAILY_EVOLUTION_CATEGORY_ACCUMULATED")) {
			String branchAux="%";
			parameters.put("SUCURSAL",branchAux );
		}	
		if (reportName.equals("SALES_BY_CATEGORIES_WITH_COST_UTILITY_MARGIN_EXCEL") || reportName.equals("REPORT_BY_CATEGORY_PRODUCT_MONTLY_EXCEL")|| reportName.equals("REPORT_BY_CATEGORY_PRODUCT_DAILY_EXCEL")) {
			Map<String, String> mapCategory = (Map<String,String>) getView().getRoot().getValue("codeCategory");
			String category="";
			if (mapCategory.get("accountItemTypeId")!=null ){
				AccountItemType currentCategory=  XPersistence.getManager().find(AccountItemType.class, (String)mapCategory.get("accountItemTypeId"));			
				category = currentCategory.getAccountItemTypeId();
				parameters.put("CATEGORIA", category);
			}
		}
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
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getFormat() {
		Types.ReportFormat reportFormat = (Types.ReportFormat)getView().getValue("reportFormat");
		return reportFormat.toString();
	}

	public void validate()
	{
		Types.ReportFormat format = (Types.ReportFormat)getView().getValue("reportFormat");
		if (format==null)
			throw new OperativeException("format_is_required");
	}

	
}