package com.powerfin.model.dto;

import java.io.InputStream;

public class ReportDTO {

	private String reportId;
	private String name;
	private String format;
	private String file;
	private InputStream jrxml;
	
	public ReportDTO()
	{
		
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public InputStream getJrxml() {
		return jrxml;
	}
	public void setJrxml(InputStream jrxml) {
		this.jrxml = jrxml;
	}
	
	
}
