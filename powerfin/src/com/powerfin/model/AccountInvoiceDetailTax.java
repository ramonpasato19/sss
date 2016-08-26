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
@Table(name = "account_invoice_detail_tax")
@View(members = "tax;"
		+ "taxBase;"
		+ "finalAmount")
public class AccountInvoiceDetailTax {

	@Id
	@Column(name = "account_invoice_detail_tax_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountInvoiceDetailTaxId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_invoice_detail_id", nullable = false)
	private AccountInvoiceDetail accountInvoiceDetail;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@NoFrame
	//@DescriptionsList(descriptionProperties="name")
	@ReferenceView(value="Simple")
	private Tax tax;

	@Column(name = "tax_base", nullable = false, precision=11, scale=2)
	@Required
	private BigDecimal taxBase;
	
	@Column(name = "tax_adjust", nullable = false, precision=11, scale=2)
	//@Required
	private BigDecimal taxAdjust;
	
	@Column(name = "tax_percentage", nullable = false, precision=11, scale=2)
	//@Required
	private BigDecimal taxPercentage;
	
	@Column(name = "amount", nullable = false, precision=11, scale=2)
	//@Required
	private BigDecimal amount;

	public String getAccountInvoiceDetailTaxId() {
		return accountInvoiceDetailTaxId;
	}

	public void setAccountInvoiceDetailTaxId(String accountInvoiceDetailTaxId) {
		this.accountInvoiceDetailTaxId = accountInvoiceDetailTaxId;
	}

	public AccountInvoiceDetail getAccountInvoiceDetail() {
		return accountInvoiceDetail;
	}

	public void setAccountInvoiceDetail(AccountInvoiceDetail accountInvoiceDetail) {
		this.accountInvoiceDetail = accountInvoiceDetail;
	}

	@Depends("taxBase, tax.percentage")
	public BigDecimal getFinalAmount() {
		BigDecimal finalAmount = BigDecimal.ZERO;
		if(getTax()!=null)
		{
			if(getTax().getPercentage()!=null)
			{
				if (getTaxBase()!=null)
					finalAmount = taxBase.multiply(tax.getPercentage()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
			}
		}
		return finalAmount;
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

	public BigDecimal getTaxAdjust() {
		return taxAdjust;
	}

	public void setTaxAdjust(BigDecimal taxAdjust) {
		this.taxAdjust = taxAdjust;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@PreCreate
	public void onCreate()
	{
		taxAdjust = BigDecimal.ZERO;
		taxPercentage = tax.getPercentage();
		amount = getFinalAmount();
	}
	
	@PreUpdate
	public void onUpdate()
	{
		taxPercentage = tax.getPercentage();
		amount = getFinalAmount();
	}

}