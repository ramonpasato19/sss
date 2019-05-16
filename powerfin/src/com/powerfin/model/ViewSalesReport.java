package com.powerfin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;

import com.powerfin.model.types.Types;

@View(members="reportFormat;"
		+ "level;"
		+ "fromDate;"
		+ "toDate;"
		+ "branch;"
		+ "accountingDate;"
		+ "codeCategory;")
public class ViewSalesReport {

	private Types.ReportFormat reportFormat;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	private Branch branch;
	
	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@Column
	private Date accountingDate;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@NoCreate
	@NoModify
	@ReferenceView("selectionTypeCategory")
	@Required
	private  AccountItemType codeCategory;
	
	@Column
	private Integer level;
	
	
	public ViewSalesReport() {
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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

	public Date getAccountingDate() {
		return accountingDate;
	}
	
	public AccountItemType getCodeCategory() {
		return codeCategory;
	}

	public void setCodeCategory(AccountItemType codeCategory) {
		this.codeCategory = codeCategory;
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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
}
