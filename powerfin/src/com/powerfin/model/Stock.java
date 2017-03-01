package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

@Entity
@Table(name="stock")
public class Stock {

	@Id
	@Column(name="stock_id")
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String stockId;

	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private AccountItem accountId;

	@ManyToOne
	@JoinColumn(name="account_invoice_id", nullable=false)
	private AccountInvoice accountInvoiceId;

	@Column(name="registrer_date")
	private Date registrerDate;

	@Column(name="quantity", precision=20, scale=2)
	private BigDecimal quantity;

	/*@Column(name="stock")
	private BigDecimal stock;
	*/
	@Column(name="value", precision=12, scale=3)
	private BigDecimal value;

	@Column(name="average_value", precision=12, scale=3)
	private BigDecimal averageValue;

	@Column(name="total_value", precision=12, scale=3)
	private BigDecimal totalValue;

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public AccountItem getAccountId() {
		return accountId;
	}

	public void setAccountId(AccountItem accountId) {
		this.accountId = accountId;
	}

	public AccountInvoice getAccountInvoiceId() {
		return accountInvoiceId;
	}

	public void setAccountInvoiceId(AccountInvoice accountInvoiceId) {
		this.accountInvoiceId = accountInvoiceId;
	}

	public Date getRegistrerDate() {
		return registrerDate;
	}

	public void setRegistrerDate(Date registrerDate) {
		this.registrerDate = registrerDate;
	}


	public BigDecimal getAverageValue() {
		return averageValue;
	}

	public void setAverageValue(BigDecimal averageValue) {
		this.averageValue = averageValue;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}



}
