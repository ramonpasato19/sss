package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="product;"
		+ "projectedAccountingDate")
public class ViewTermReport {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;

	@Column
	private Date projectedAccountingDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchProduct.SearchTermProducts")
	private Product product;
	
	public ViewTermReport() {
		
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

}
