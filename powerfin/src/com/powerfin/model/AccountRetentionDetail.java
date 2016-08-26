package com.powerfin.model;

import java.math.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * The persistent class for the account_invoice_retention_detail database table.
 * 
 */
@Entity
@Table(name = "account_retention_detail")
@Views({
@View(members = "retentionConcept;"
		+ "subtotal, vat;")
})
public class AccountRetentionDetail {

	@Id
	@Column(name = "account_retention_detail_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountRetentionDetailId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "retention_concept_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	private RetentionConcept retentionConcept;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_retention_id", nullable = false)
	private AccountRetention accountRetention;
	
	@Column(name = "amount", nullable = false, precision=11, scale=2)
	private BigDecimal amount;
	
	@Column(name = "tax_percentage", nullable = false, precision=5, scale=2)
	private BigDecimal taxPercentage;
	
	@Column(name = "final_amount", nullable = false, precision=11, scale=2)
	@ReadOnly
	private BigDecimal finalAmount;
	
	@Transient
	//@ReadOnly
	private BigDecimal subtotal;
	
	@Transient
	//@ReadOnly
	private BigDecimal vat;
	
	public AccountRetentionDetail() {
		
	}

	public String getAccountRetentionDetailId() {
		return accountRetentionDetailId;
	}

	public void setAccountRetentionDetailId(String accountRetentionDetailId) {
		this.accountRetentionDetailId = accountRetentionDetailId;
	}

	public RetentionConcept getRetentionConcept() {
		return retentionConcept;
	}

	public void setRetentionConcept(RetentionConcept retentionConcept) {
		this.retentionConcept = retentionConcept;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}

	public AccountRetention getAccountRetention() {
		return accountRetention;
	}

	public void setAccountRetention(AccountRetention accountRetention) {
		this.accountRetention = accountRetention;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}
	
	@PreCreate
	public void onCreate()
	{
		if (retentionConcept!=null)
		{
			if (retentionConcept.getTypeRetention().equals("RENT"))
			{
				this.amount = getSubtotal();
				this.taxPercentage = retentionConcept.getPercentage();
				this.finalAmount = amount.multiply(taxPercentage).divide(new BigDecimal(100));
				this.finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
			}
			else
			{
				this.amount = getVat();
				this.taxPercentage = retentionConcept.getPercentage();
				this.finalAmount = amount.multiply(taxPercentage).divide(new BigDecimal(100));
				this.finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
			}
		}
	}
}