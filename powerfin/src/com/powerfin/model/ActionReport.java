package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the category database table.
 * 
 */
@Entity
@Table(name="action_report")
@Views({
	@View(members="actionReportId; "
			+ "reports;")
})
public class ActionReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="action_report_id", unique=true, nullable=false, length=100)
	private String actionReportId;

	@Column(nullable=false, length=100)
	@Required
	private String reports;

	public ActionReport() {
	}

	public String getActionReportId() {
		return actionReportId;
	}

	public void setActionReportId(String actionReportId) {
		this.actionReportId = actionReportId;
	}

	public String getReports() {
		return reports;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}

}