package com.powerfin.model;

import java.io.*;

import javax.persistence.*;


/**
 * The persistent class for the invoice_payment_method database table.
 * 
 */
@Entity
@Table(name="invoice_payment_method")
public class InvoicePaymentMethod implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="invoice_payment_method_id", unique=true, nullable=false, length=3)
	private String invoicePaymentMethodId;

	@Column(nullable=false, length=100)
	private String name;

	public InvoicePaymentMethod() {
	}

	public String getInvoicePaymentMethodId() {
		return invoicePaymentMethodId;
	}

	public void setInvoicePaymentMethodId(String invoicePaymentMethodId) {
		this.invoicePaymentMethodId = invoicePaymentMethodId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}