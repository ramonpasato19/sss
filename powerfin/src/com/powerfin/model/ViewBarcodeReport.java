package com.powerfin.model;

import javax.persistence.Column;

import org.openxava.annotations.View;

import com.powerfin.model.types.Types;

@View(members="reportFormat;"
		+ "itemsProduct;"
		+ "numPrint;")
public class ViewBarcodeReport {
	
	private Types.ReportFormat reportFormat;

	@Column
	private Integer numPrint;
	

	@Column(nullable=false, length=300)
	private String itemsProduct;


	public ViewBarcodeReport() {
	}


	public Types.ReportFormat getReportFormat() {
		return reportFormat;
	}


	public void setReportFormat(Types.ReportFormat reportFormat) {
		this.reportFormat = reportFormat;
	}


	public Integer getNumPrint() {
		return numPrint;
	}


	public void setNumPrint(Integer numPrint) {
		this.numPrint = numPrint;
	}


	public String getItemsProduct() {
		return itemsProduct;
	}


	public void setItemsProduct(String itemsProduct) {
		this.itemsProduct = itemsProduct;
	}
	
	
	
}
