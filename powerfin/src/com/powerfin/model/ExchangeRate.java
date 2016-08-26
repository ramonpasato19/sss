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
@Table(name="exchange_rate")
@View(members="exchangeRateId;"
		+ "currency;"
		+ "exchangeRate;"
		+ "exchangeRatePurchase;"
		+ "exchangeRateSale")
public class ExchangeRate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="exchange_rate_id", unique=true, nullable=false, length=3)
	@Required
	private String exchangeRateId;

	@Column(name="exchange_rate", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRate;

	@Column(name="exchange_rate_purchase", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRatePurchase;

	@Column(name="exchange_rate_sale", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRateSale;

	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="foreign_currency_id", nullable=false)
	@DescriptionsList(descriptionProperties="currencyId")
	@Required
	private Currency currency;

	public ExchangeRate() {
	}

	public String getExchangeRateId() {
		return this.exchangeRateId;
	}

	public void setExchangeRateId(String exchangeRateId) {
		this.exchangeRateId = exchangeRateId;
	}

	public BigDecimal getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getExchangeRatePurchase() {
		return this.exchangeRatePurchase;
	}

	public void setExchangeRatePurchase(BigDecimal exchangeRatePurchase) {
		this.exchangeRatePurchase = exchangeRatePurchase;
	}

	public BigDecimal getExchangeRateSale() {
		return this.exchangeRateSale;
	}

	public void setExchangeRateSale(BigDecimal exchangeRateSale) {
		this.exchangeRateSale = exchangeRateSale;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}