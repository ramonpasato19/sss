package com.powerfin.helper;

import java.io.*;

import net.sf.jasperreports.engine.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.File;

public class ReportHelper {

	private static Log log = LogFactory.getLog(Report.class);
	private final static String FOOTER_JRXML_REPORT = "FOOTER";
	
	public static InputStream getJRXML(Report report) throws Exception
	{
		ByteArrayInputStream bis = null;
		
		if (report==null)
			throw new InternalException("report_is_null");
		
		File file = XPersistence.getManager().find(File.class, report.getFile());
		
		if (file==null)
			throw new InternalException("file_not_found", report.getFile());
		log.info("######## FILE: " + file.getName());
		bis = new ByteArrayInputStream(file.getData());
		return bis;
	}
	
	public static JasperReport getFooterJasperReport() throws Exception
	{
		return getJasperReport(findReportByName(ReportHelper.FOOTER_JRXML_REPORT));
	}
	
	private static JasperReport getJasperReport(Report report) throws Exception
	{
		return JasperCompileManager.compileReport(getJRXML(report));
	}

	public static String getFormat(String reportName) throws Exception {
		Report report = XPersistence.getManager().find(Report.class, reportName.toUpperCase());
		if (report==null)
			throw new InternalException("report_not_found", reportName);
		return report.getFormat();
	}
	
	public static Report findReportByName(String reportName) throws Exception {
		Report report = XPersistence.getManager().find(Report.class, reportName.toUpperCase());
		if (report==null)
			throw new InternalException("report_not_found", reportName);
		log.info("######## REPORT: " + report.getReportId());
		log.info("######## REPORT FORMAT: " + report.getFormat());
		return report;
	}
}
