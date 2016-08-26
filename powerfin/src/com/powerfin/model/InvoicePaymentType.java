package com.powerfin.model;

import java.io.*;

import javax.persistence.*;


/**
 * The persistent class for the invoice_payment_type database table.
 * 
 */
@Entity
@Table(name="invoice_payment_type")
public class InvoicePaymentType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="invoice_payment_type_id", unique=true, nullable=false, length=2)
	private String invoicePaymentTypeId;

	@Column(nullable=false, length=100)
	private String name;

	public InvoicePaymentType() {
	}

	public String getInvoicePaymentTypeId() {
		return invoicePaymentTypeId;
	}

	public void setInvoicePaymentTypeId(String invoicePaymentTypeId) {
		this.invoicePaymentTypeId = invoicePaymentTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}