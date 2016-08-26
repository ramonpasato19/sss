package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the invoice_provider_type database table.
 * 
 */
@Entity
@Table(name="invoice_provider_type")
@View(members="invoiceProviderTypeId;name")
public class InvoiceProviderType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="invoice_provider_type_id", unique=true, nullable=false, length=2)
	private String invoiceProviderTypeId;

	@Column(nullable=false, length=100)
	@DisplaySize(40)
	private String name;

	public InvoiceProviderType() {
	}

	public String getInvoiceProviderTypeId() {
		return invoiceProviderTypeId;
	}

	public void setInvoiceProviderTypeId(String invoiceProviderTypeId) {
		this.invoiceProviderTypeId = invoiceProviderTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}