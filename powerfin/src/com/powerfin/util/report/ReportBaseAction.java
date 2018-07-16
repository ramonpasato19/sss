package com.powerfin.util.report;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import javax.servlet.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

import net.sf.jasperreports.engine.*;

public abstract class ReportBaseAction extends JasperReportBaseAction {

	private static Log log = LogFactory.getLog(ReportBaseAction.class);
	
	private String modelName;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute() throws Exception {

		ServletContext application = getRequest().getSession().getServletContext();
		System.setProperty("jasper.reports.compile.class.path",					 
			application.getRealPath("/WEB-INF/lib/jasperreports.jar") +
			System.getProperty("path.separator") + 
			application.getRealPath("/WEB-INF/classes/")
		);
		
		String reportName = getReportName();
		if (!getFormat().equals(JasperReportBaseAction.PDF))
			reportName = reportName+"_"+getFormat().toUpperCase();
		
		Report report = ReportHelper.findReportByName(reportName);
		setFileName(report.getName()+" "+new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()));
		setFormat(report.getFormat().toLowerCase());
		JasperReport jReport = JasperCompileManager.compileReport(ReportHelper.getJRXML(report));
		Map parameters = getParameters();
		addSubReports(jReport, parameters);
		
		log.info("######## PARAMETERS: " + parameters.toString());
		JRDataSource ds = getDataSource();
		JasperPrint jprint = null;
		if (ds == null) {
			Connection con = null;
			try {
				con = DataSourceConnectionProvider.getByComponent(modelName).getConnection();
				con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); // To avoid freezing the application with some reports in some databases
				// If the schema is changed through URL or XPersistence.setDefaultSchema, the connection
				// contains the original catalog (schema) instead of the new one, thus rendering the
				// wrong data on the report. This is a fix for such behavior.
				if (!Is.emptyString(XPersistence.getDefaultSchema())) {
					con.setCatalog(XPersistence.getDefaultSchema());
				}
				jprint = JasperFillManager.fillReport(jReport, parameters, con);
			} finally {
				con.close();
			}
		}
		else {
			jprint = JasperFillManager.fillReport(jReport, parameters, ds);
		}		
		getRequest().getSession().setAttribute("xava.report.jprint", jprint);
		getRequest().getSession().setAttribute("xava.report.format", getFormat());
		getRequest().getSession().setAttribute("xava.report.filename", getFileName()); 
	}
	
	@Override
	protected JRDataSource getDataSource() throws Exception {
		return null;
	}

	@Override
	protected String getJRXML() throws Exception {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Map getParameters() throws Exception {
		return null;
	}

	public void setModel(String modelName) { //  to obtain a JDCB connection, if required
		this.modelName = modelName;
	}

	@SuppressWarnings({ "rawtypes" })
	public void addDefaultParameters(Map parameters) throws Exception
	{
		addSchemaParameter(parameters);
		addOrganizationNameParameter(parameters);
		addUserParameter(parameters);
		addCurrentAccountingDateParameter(parameters);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addOrganizationIdParameter(Map parameters)
	{
		parameters.put("ORG_ID", CompanyHelper.getDefaultPerson().getPersonId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addOrganizationNameParameter(Map parameters)
	{
		parameters.put("ORG_NAME", CompanyHelper.getDefaultPerson().getName());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addSchemaParameter(Map parameters)
	{
		parameters.put("SCHEMA", XPersistence.getDefaultSchema().toUpperCase());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addUserParameter(Map parameters)
	{
		parameters.put("USER", Users.getCurrentUserInfo().getId());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addCurrentAccountingDateParameter(Map parameters)
	{
		parameters.put("CURRENT_ACCOUNTING_DATE", CompanyHelper.getCurrentAccountingDate());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addFooterSubreportParameter(Map parameters) throws Exception
	{
		parameters.put("SUBREPORT_FOOTER", ReportHelper.getFooterJasperReport());
	}

	abstract protected String getReportName() throws Exception;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSubReports(JasperReport jReport, Map parameters) throws Exception{
		JRParameter[] parameter=jReport.getParameters();
		for(int j=0;j<parameter.length;j++){
			if(parameter[j].getDescription()!=null && parameter[j].getDescription().toUpperCase().equals("SUBREPORT")){				
				Report report = ReportHelper.findReportByName(parameter[j].getName().toUpperCase());
				JasperReport jsubReport = JasperCompileManager.compileReport(ReportHelper.getJRXML(report));
				addSubReports(jsubReport, parameters);
				parameters.put(parameter[j].getName().toUpperCase(), jsubReport);
			}
		}
	}
}
