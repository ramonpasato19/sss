package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@Views({ @View(members = "fromDate, toDate;" + "downloadInvoices;" + "notDownloadInvoices;"),
		@View(name = "ViewDownloadInvoicePrestashop", members = "fromDate, toDate;" + "downloadInvoices;"
				+ "notDownloadInvoices;") })
public class ViewDownloadInvoice {

	@Column
	Date fromDate;

	@Column
	Date toDate;

	@Transient
	@ReadOnly
	@Column(length = 100)
	String downloadInvoices;

	@Transient
	@ReadOnly
	@Column(length = 100)
	String notDownloadInvoices;

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

	public String getDownloadInvoices() {
		return downloadInvoices;
	}

	public void setDownloadInvoices(String downloadInvoices) {
		this.downloadInvoices = downloadInvoices;
	}

	public String getNotDownloadInvoices() {
		return notDownloadInvoices;
	}

	public void setNotDownloadInvoices(String notDownloadInvoices) {
		this.notDownloadInvoices = notDownloadInvoices;
	}

}