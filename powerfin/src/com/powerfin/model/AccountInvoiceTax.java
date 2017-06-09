package com.powerfin.model;

import java.math.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * The persistent class for the account_invoice_detail database table.
 * 
 */
@Entity
@Table(name = "account_invoice_tax")
@View(members = "accountInvoiceTaxId;"
		+ "tax;"
		+ "taxPercentage;"
		+ "taxBase;"
		+ "taxAmount")
public class AccountInvoiceTax {

	@Id
	@Column(name = "account_invoice_tax_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountInvoiceTaxId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_invoice_id", nullable = false)
	private AccountInvoice accountInvoice;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@NoFrame
	//@DescriptionsList(descriptionProperties="name")
	@ReferenceView(value="Simple")
	private Tax tax;
	
	@Column(name = "tax_percentage", nullable = false, precision=11, scale=2)
	@Required
	private BigDecimal taxPercentage;
	
	@Column(name = "tax_base", nullable = false, precision=11, scale=2)
	@Required
	private BigDecimal taxBase;
	
	@Column(name = "tax_amount", nullable = false, precision=11, scale=2)
	//@Required
	private BigDecimal taxAmount;
	
	public AccountInvoiceTax()
	{
		
	}
	public AccountInvoiceTax(Tax tax, BigDecimal taxBase)
	{
		this.tax = tax;
		this.taxBase = taxBase;
		taxPercentage = tax.getPercentage();
		taxAmount = calculateTaxAmount();
	}
	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public BigDecimal getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(BigDecimal taxBase) {
		this.taxBase = taxBase;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public String getAccountInvoiceTaxId() {
		return accountInvoiceTaxId;
	}

	public void setAccountInvoiceTaxId(String accountInvoiceTaxId) {
		this.accountInvoiceTaxId = accountInvoiceTaxId;
	}

	public AccountInvoice getAccountInvoice() {
		return accountInvoice;
	}

	public void setAccountInvoice(AccountInvoice accountInvoice) {
		this.accountInvoice = accountInvoice;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	@PreCreate
	public void onCreate()
	{	
		taxPercentage = tax.getPercentage();
		taxAmount = calculateTaxAmount();
	}
	
	@PreUpdate
	public void onUpdate()
	{
		taxPercentage = tax.getPercentage();
		taxAmount = calculateTaxAmount();
	}
	
	public BigDecimal calculateTaxAmount() {
		BigDecimal value = BigDecimal.ZERO;
		if(getTax()!=null)
		{
			if(getTax().getPercentage()!=null)
			{
				if (getTaxBase()!=null)
					value = taxBase.multiply(tax.getPercentage()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
			}
		}
		return value;
	}
}