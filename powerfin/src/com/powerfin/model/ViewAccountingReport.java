package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.types.Types;

@View(members="reportFormat; level; forEvolution[accountingDate]; forAnalysis[fromDate; toDate];")
public class ViewAccountingReport {

	private Types.ReportFormat reportFormat;
	
	@Column
	private Date accountingDate;
	
	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@Column
	private Integer level;
	
	@Column
	@ReadOnly
	@DisplaySize(1)
	private String forAnalysis;
	
	public ViewAccountingReport() {
		
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Types.ReportFormat getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(Types.ReportFormat reportFormat) {
		this.reportFormat = reportFormat;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getForAnalysis() {
		return forAnalysis;
	}

	public void setForAnalysis(String forAnalysis) {
		this.forAnalysis = forAnalysis;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	
}
