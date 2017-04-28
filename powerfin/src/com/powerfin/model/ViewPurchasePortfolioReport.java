package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="broker;"
		+ "fromDate;"
		+ "toDate;"
		+ "projectedAccountingDate")
public class ViewPurchasePortfolioReport {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@Column
	private Date projectedAccountingDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("ShortReference")
	@SearchAction("SearchPerson.SearchBrokerPurchasePortfolio")
	private Person broker;

	public ViewPurchasePortfolioReport() {
		
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

	public Person getBroker() {
		return broker;
	}

	public void setBroker(Person broker) {
		this.broker = broker;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

}
