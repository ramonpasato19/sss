package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="product;"
		+ "projectedAccountingDate")
public class ViewPayableReport {

	@Column
	private Date projectedAccountingDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchProduct.SearchPayableProduct")
	private Product product;
	
	public ViewPayableReport() {
		
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
