package com.powerfin.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import org.openxava.annotations.View;

@View(members = "fromDate; toDate")
public class ViewPortfolioRecoveryManagementReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column
	private Date fromDate;

	@Column
	private Date toDate;

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

}
