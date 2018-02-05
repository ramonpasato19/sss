package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;

import org.openxava.annotations.*;

import java.math.BigDecimal;


/**
 * The persistent class for the exchange_rate database table.
 * 
 */
@Entity
@Table(name="loan_collection_fee")
@View(members="loanCollectionFeeId;"
		+ "product;"
		+ "fromAmount;"
		+ "toAmount;"
		+ "fromDays;"
		+ "toDays;"
		+ "value;")
public class LoanCollectionFee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="loan_collection_fee_id", unique=true, nullable=false, length=3)
	@Required
	private String loanCollectionFeeId;

	@Column(name="from_days", nullable=false)
	@Required
	private Integer fromDays;

	@Column(name="to_days", nullable=false)
	@Required
	private Integer toDays;

	@Column(name="from_amount", nullable=false, precision=11, scale=2)
	@Required
	private BigDecimal fromAmount;
	
	@Column(name="to_amount", nullable=false, precision=11, scale=2)
	@Required
	private BigDecimal toAmount;
	
	@Column(name="value", nullable=false, precision=5, scale=2)
	@Required
	private BigDecimal value;

	@ManyToOne
	@JoinColumn(name="product_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Product product;
	
	public LoanCollectionFee() {
	}

	public String getLoanCollectionFeeId() {
		return loanCollectionFeeId;
	}

	public void setLoanCollectionFeeId(String loanCollectionFeeId) {
		this.loanCollectionFeeId = loanCollectionFeeId;
	}

	public Integer getFromDays() {
		return fromDays;
	}

	public void setFromDays(Integer fromDays) {
		this.fromDays = fromDays;
	}

	public Integer getToDays() {
		return toDays;
	}

	public void setToDays(Integer toDays) {
		this.toDays = toDays;
	}

	public BigDecimal getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(BigDecimal fromAmount) {
		this.fromAmount = fromAmount;
	}

	public BigDecimal getToAmount() {
		return toAmount;
	}

	public void setToAmount(BigDecimal toAmount) {
		this.toAmount = toAmount;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}