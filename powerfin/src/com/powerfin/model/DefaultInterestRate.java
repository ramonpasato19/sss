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
@Table(name="default_interest_rate")
@View(members="defaultInterestRateId;"
		+ "fromDays;"
		+ "toDays;"
		+ "rate;")
public class DefaultInterestRate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="default_interest_rate_id", unique=true, nullable=false, length=3)
	@Required
	private String defaultInterestRateId;

	@Column(name="from_days", nullable=false)
	@Required
	private Integer fromDays;

	@Column(name="to_days", nullable=false)
	@Required
	private Integer toDays;

	@Column(name="rate", nullable=false, precision=5, scale=2)
	@Required
	private BigDecimal rate;

	public DefaultInterestRate() {
	}

	public String getDefaultInterestRateId() {
		return defaultInterestRateId;
	}

	public void setDefaultInterestRateId(String defaultInterestRateId) {
		this.defaultInterestRateId = defaultInterestRateId;
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	

}