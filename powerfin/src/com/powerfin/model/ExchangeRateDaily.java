package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import java.math.BigDecimal;
import java.util.*;


/**
 * The persistent class for the exchange_rate database table.
 * 
 */
@Entity
@Table(name="exchange_rate_daily")
@Views({
@View(members="currency;"
		+ "fromDate;"
		+ "toDate;"
		+ "exchangeRate;"
		+ "exchangeRatePurchase;"
		+ "exchangeRateSale"),
@View(name ="NewExchangeRateDaily", members=""
		+ "currency;"
		+ "fromDate;"
		+ "toDate;"
		+ "exchangeRate;"
		+ "previousExchangeRate;"
		+ "exchangeRatePurchase;"
		+ "exchangeRateSale"),
})
@Tab(properties="currency.currencyId, fromDate, toDate, exchangeRate")
public class ExchangeRateDaily implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "exchange_rate_daily_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@ReadOnly
	private String exchangeRateDailyId;

	@Temporal(TemporalType.DATE)
	@Column(name="from_date", unique=true, nullable=false)
	@ReadOnly(forViews="NewExchangeRateDaily")
	private Date fromDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="to_date", unique=true, nullable=false)
	@ReadOnly(forViews="NewExchangeRateDaily")
	private Date toDate;
	
	@Column(name="exchange_rate", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRate;
	
	@Column(name="previous_exchange_rate", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal previousExchangeRate;

	@Column(name="exchange_rate_purchase", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRatePurchase;

	@Column(name="exchange_rate_sale", nullable=false, precision=11, scale=7)
	@Required
	private BigDecimal exchangeRateSale;

	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="currency_id", nullable=false)
	@DescriptionsList(descriptionProperties="currencyId")
	@Required
	private Currency currency;

	public ExchangeRateDaily() {
	}

	public String getExchangeRateDailyId() {
		return exchangeRateDailyId;
	}

	public void setExchangeRateDailyId(String exchangeRateDailyId) {
		this.exchangeRateDailyId = exchangeRateDailyId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getExchangeRatePurchase() {
		return exchangeRatePurchase;
	}

	public void setExchangeRatePurchase(BigDecimal exchangeRatePurchase) {
		this.exchangeRatePurchase = exchangeRatePurchase;
	}

	public BigDecimal getExchangeRateSale() {
		return exchangeRateSale;
	}

	public void setExchangeRateSale(BigDecimal exchangeRateSale) {
		this.exchangeRateSale = exchangeRateSale;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getPreviousExchangeRate() {
		return previousExchangeRate;
	}

	public void setPreviousExchangeRate(BigDecimal previousExchangeRate) {
		this.previousExchangeRate = previousExchangeRate;
	}

}