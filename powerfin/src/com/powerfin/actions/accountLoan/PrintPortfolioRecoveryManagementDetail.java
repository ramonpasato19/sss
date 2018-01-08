package com.powerfin.actions.accountLoan;

import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.ActionReportHelper;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintPortfolioRecoveryManagementDetail extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameters() throws Exception {

		Map<String, String> accountLoan = (Map<String, String>) getView().getValue("accountLoan");
		if (accountLoan.get("accountId") == null || accountLoan.get("accountId").isEmpty()) {
			throw new OperativeException("select_the_loan_to_manage");
		}

		Integer portfolioRecoveryManagementId = getView().getRoot().getValueInt("portfolioRecoveryManagementId");
		Map parameters = new HashMap();
		if (portfolioRecoveryManagementId != null) {
			parameters.put("PORTFOLIO_RECOVERY_MANAGEMENT_ID", portfolioRecoveryManagementId);
		}
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