package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_portfolio database table.
 * 
 */
@Entity
@Table(name="account_portfolio")
@NamedQuery(name="AccountPortfolio.findAll", query="SELECT a FROM AccountPortfolio a")
public class AccountPortfolio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private String accountId;

	@Column(name="purchase_amount", precision=11, scale=2)
	private BigDecimal purchaseAmount;

	@Column(name="purchase_rate", precision=5, scale=2)
	private BigDecimal purchaseRate;

	@Column(name="purchase_spread", precision=11, scale=2)
	private BigDecimal purchaseSpread;

	@Column(name="purchased_from_person", precision=11, scale=2)
	private BigDecimal purchasedFromPerson;

	@Column(name="sale_amount", precision=11, scale=2)
	private BigDecimal saleAmount;

	@Column(name="sale_rate", precision=5, scale=2)
	private BigDecimal saleRate;

	@Column(name="sale_spread", precision=11, scale=2)
	private BigDecimal saleSpread;

	@Column(name="sold_to_person")
	private Integer soldToPerson;

	@Column(name="status_id", length=3)
	private String statusId;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	//bi-directional many-to-one association to Negotiation
	@ManyToOne
	@JoinColumn(name="purchase_negotiation_id", nullable=true)
	private Negotiation purchaseNegotiation;
	
	//bi-directional many-to-one association to Negotiation
	@ManyToOne
	@JoinColumn(name="sale_negotiation_id", nullable=true)
	private Negotiation saleNegotiation;
		
	public AccountPortfolio() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getPurchaseAmount() {
		return this.purchaseAmount;
	}

	public void setPurchaseAmount(BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public BigDecimal getPurchaseRate() {
		return this.purchaseRate;
	}

	public void setPurchaseRate(BigDecimal purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public BigDecimal getPurchaseSpread() {
		return this.purchaseSpread;
	}

	public void setPurchaseSpread(BigDecimal purchaseSpread) {
		this.purchaseSpread = purchaseSpread;
	}

	public BigDecimal getPurchasedFromPerson() {
		return this.purchasedFromPerson;
	}

	public void setPurchasedFromPerson(BigDecimal purchasedFromPerson) {
		this.purchasedFromPerson = purchasedFromPerson;
	}

	public BigDecimal getSaleAmount() {
		return this.saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	public BigDecimal getSaleRate() {
		return this.saleRate;
	}

	public void setSaleRate(BigDecimal saleRate) {
		this.saleRate = saleRate;
	}

	public BigDecimal getSaleSpread() {
		return this.saleSpread;
	}

	public void setSaleSpread(BigDecimal saleSpread) {
		this.saleSpread = saleSpread;
	}

	public Integer getSoldToPerson() {
		return this.soldToPerson;
	}

	public void setSoldToPerson(Integer soldToPerson) {
		this.soldToPerson = soldToPerson;
	}

	public String getStatusId() {
		return this.statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Negotiation getPurchaseNegotiation() {
		return purchaseNegotiation;
	}

	public void setPurchaseNegotiation(Negotiation purchaseNegotiation) {
		this.purchaseNegotiation = purchaseNegotiation;
	}

	public Negotiation getSaleNegotiation() {
		return saleNegotiation;
	}

	public void setSaleNegotiation(Negotiation saleNegotiation) {
		this.saleNegotiation = saleNegotiation;
	}
}