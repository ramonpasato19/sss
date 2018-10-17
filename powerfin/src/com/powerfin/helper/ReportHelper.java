package com.powerfin.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.jpa.XPersistence;

import com.powerfin.exception.InternalException;
import com.powerfin.model.File;
import com.powerfin.model.Report;
import com.powerfin.model.dto.ReportDTO;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

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
			throw new InternalException("file_not_found", report.getFileId(), report.getFile());
		log.info("######## FILE: " + file.getName());
		bis = new ByteArrayInputStream(file.getData());
		return bis;
	}
	
	public static JasperReport getFooterJasperReport() throws Exception
	{
		return getJasperReport(findReportByName(ReportHelper.FOOTER_JRXML_REPORT));
	}
	
	private static JasperReport getJasperReport(ReportDTO dto) throws Exception
	{	
		return JasperCompileManager.compileReport(dto.getJrxml());
	}

	public static String getFormat(String reportName) throws Exception {
		Report report = XPersistence.getManager().find(Report.class, reportName.toUpperCase());
		if (report==null)
			throw new InternalException("report_not_found", reportName);
		return report.getFormat();
	}
	
	public static ReportDTO findReportByName(String reportName) throws Exception {
		log.info("######## REPORT TO SEARCH: " + reportName);
		Report report = XPersistence.getManager().find(Report.class, reportName.toUpperCase());
		ReportDTO dto;
		if (report == null)
		{
			try
			{
				Object[] oReport = (Object[]) XPersistence.getManager()
						.createNativeQuery("SELECT report_id, name, format, file FROM public.report "
						+ "WHERE upper(report_id) = '"+reportName.toUpperCase()+"' ")
						.getSingleResult();
				
				dto = new ReportDTO();
				dto.setReportId((String) oReport[0]);
				dto.setName((String) oReport[1]);
				dto.setFormat((String) oReport[2]);				
				dto.setFile((String) oReport[3]);
				
				if (dto.getFile() == null)
					throw new InternalException("file_is_required", reportName);
				
				byte[] oFile = null;
				try
				{
					oFile = (byte[]) XPersistence.getManager().createNativeQuery("SELECT data FROM public.oxfiles "
						+ "WHERE upper(id) = '"+dto.getFile().toUpperCase()+"' ")
						.getSingleResult();
				} catch(javax.persistence.NoResultException e)
				{
					throw new InternalException("file_not_found", reportName, dto.getFile());
				}
				log.info("######## REPORT FROM PUBLIC SCHEMA");
				log.info("######## FILE: " + dto.getFile());

				InputStream bis = new ByteArrayInputStream(oFile );
			    dto.setJrxml(bis);
			}
			catch (javax.persistence.NoResultException e)
			{
				throw new InternalException("public_report_not_found", reportName);
			}
			
		}
		else
		{
			log.info("######## REPORT FROM CURRENT SCHEMA");
			dto = new ReportDTO();
			dto.setReportId(report.getReportId());
			dto.setFormat(report.getFormat());
			dto.setJrxml(ReportHelper.getJRXML(report));
			dto.setName(report.getName());
		}
		log.info("######## REPORT FORMAT: " + dto.getFormat());
		return dto;
	}
}
