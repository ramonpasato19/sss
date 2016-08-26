package com.powerfin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * The persistent class for the account_invoice_payment database table.
 * 
 */
@Entity
@Table(name = "account_invoice_payment")
@Views({
@View(members = "invoicePaymentMethod;")
})
public class AccountInvoicePayment {

	@Id
	@Column(name = "account_invoice_payment_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountInvoicePaymentId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_invoice_id", nullable = false)
	private AccountInvoice accountInvoice;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_payment_method_id", nullable = false)
	@DescriptionsList(descriptionProperties="name")
	private InvoicePaymentMethod invoicePaymentMethod;

	public AccountInvoicePayment() {
		
	}

	public String getAccountInvoicePaymentId() {
		return accountInvoicePaymentId;
	}

	public void setAccountInvoicePaymentId(String accountInvoicePaymentId) {
		this.accountInvoicePaymentId = accountInvoicePaymentId;
	}

	public AccountInvoice getAccountInvoice() {
		return accountInvoice;
	}

	public void setAccountInvoice(AccountInvoice accountInvoice) {
		this.accountInvoice = accountInvoice;
	}

	public InvoicePaymentMethod getInvoicePaymentMethod() {
		return invoicePaymentMethod;
	}

	public void setInvoicePaymentMethod(InvoicePaymentMethod invoicePaymentMethod) {
		this.invoicePaymentMethod = invoicePaymentMethod;
	}
	
}