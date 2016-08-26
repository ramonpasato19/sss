package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the invoice_voucher_type database table.
 * 
 */
@Entity
@Table(name="invoice_voucher_type")
@View(members="invoiceVoucherTypeId;name")
public class InvoiceVoucherType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="invoice_voucher_type_id", unique=true, nullable=false, length=3)
	private String invoiceVoucherTypeId;

	@Column(nullable=false, length=100)
	@DisplaySize(40)
	private String name;

	public InvoiceVoucherType() {
	}

	public String getInvoiceVoucherTypeId() {
		return invoiceVoucherTypeId;
	}

	public void setInvoiceVoucherTypeId(String invoiceVoucherTypeId) {
		this.invoiceVoucherTypeId = invoiceVoucherTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}