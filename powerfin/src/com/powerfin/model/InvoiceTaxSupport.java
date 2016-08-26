package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the invoice_tax_support database table.
 * 
 */
@Entity
@Table(name="invoice_tax_support")
@View(members="invoiceTaxSupportId;name")
public class InvoiceTaxSupport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="invoice_tax_support_id", unique=true, nullable=false, length=3)
	private String invoiceTaxSupportId;

	@Column(nullable=false, length=100)
	@DisplaySize(40)
	private String name;

	public InvoiceTaxSupport() {
	}

	public String getInvoiceTaxSupportId() {
		return invoiceTaxSupportId;
	}

	public void setInvoiceTaxSupportId(String invoiceTaxSupportId) {
		this.invoiceTaxSupportId = invoiceTaxSupportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}