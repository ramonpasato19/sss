package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the account_paytable database table.
 * 
 */
@Entity
@Table(name="account_sold_paytable")

public class AccountSoldPaytable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_sold_paytable_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountSoldPaytableId;

	@Column(name="account_id", unique=true, nullable=false)
	private String accountId;
	
	@Column(unique=true, nullable=false)
	private Integer subaccount;
	
	@Column(name = "sale_subaccount", unique=true, nullable=false)
	private Integer saleSubaccount;
	
	@Column(precision=11, scale=2)
	private BigDecimal capital;

	@Column(name="capital_reduced", precision=11, scale=2)
	private BigDecimal capitalReduced;

	@Column(precision=11, scale=2)
	private BigDecimal commission;

	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	private Date dueDate;

	@Column(precision=11, scale=2)
	private BigDecimal insurance;

	@Column(name="insurance_mortgage", precision=11, scale=2)
	private BigDecimal insuranceMortgage;

	@Column(precision=11, scale=2)
	private BigDecimal interest;

	@Column(name="purchase_spread", precision=11, scale=2)
	private BigDecimal purchaseSpread;
	
	@Column(name="utility_sale_portfolio", precision=11, scale=2)
	private BigDecimal utilitySalePortfolio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="payment_date")
	private Date paymentDate;

	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date")
	private Date lastPaymentDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_collection")
	private Date lastPaymentDateCollection;
	
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_default_int")
	private Date lastPaymentDateDefaultInterest;
	
	@Column(name="provision_days")
	private Integer provisionDays;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
		
	public AccountSoldPaytable() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getCapital() {
		return this.capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getCapitalReduced() {
		return this.capitalReduced;
	}

	public void setCapitalReduced(BigDecimal capitalReduced) {
		this.capitalReduced = capitalReduced;
	}

	public BigDecimal getCommission() {
		return this.commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getInsurance() {
		return this.insurance;
	}

	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}

	public BigDecimal getInsuranceMortgage() {
		return this.insuranceMortgage;
	}

	public void setInsuranceMortgage(BigDecimal insuranceMortgage) {
		this.insuranceMortgage = insuranceMortgage;
	}

	public BigDecimal getInterest() {
		return this.interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Integer getProvisionDays() {
		return provisionDays;
	}

	public void setProvisionDays(Integer provisionDays) {
		this.provisionDays = provisionDays;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getAccountSoldPaytableId() {
		return accountSoldPaytableId;
	}

	public void setAccountSoldPaytableId(String accountSoldPaytableId) {
		this.accountSoldPaytableId = accountSoldPaytableId;
	}

	public Integer getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getPurchaseSpread() {
		return purchaseSpread;
	}

	public void setPurchaseSpread(BigDecimal purchaseSpread) {
		this.purchaseSpread = purchaseSpread;
	}

	public BigDecimal getUtilitySalePortfolio() {
		return utilitySalePortfolio;
	}

	public void setUtilitySalePortfolio(BigDecimal utilitySalePortfolio) {
		this.utilitySalePortfolio = utilitySalePortfolio;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public Date getLastPaymentDateCollection() {
		return lastPaymentDateCollection;
	}

	public void setLastPaymentDateCollection(Date lastPaymentDateCollection) {
		this.lastPaymentDateCollection = lastPaymentDateCollection;
	}

	public Date getLastPaymentDateDefaultInterest() {
		return lastPaymentDateDefaultInterest;
	}

	public void setLastPaymentDateDefaultInterest(Date lastPaymentDateDefaultInterest) {
		this.lastPaymentDateDefaultInterest = lastPaymentDateDefaultInterest;
	}

	public Integer getSaleSubaccount() {
		return saleSubaccount;
	}

	public void setSaleSubaccount(Integer saleSubaccount) {
		this.saleSubaccount = saleSubaccount;
	}

	public BigDecimal getTotalQuota() {
		BigDecimal totalQuotaAux = BigDecimal.ZERO;
		if (this.capital!=null)
			totalQuotaAux=totalQuotaAux.add(capital);
		if (this.interest!=null)
			totalQuotaAux=totalQuotaAux.add(interest);
		if (this.insurance!=null)
			totalQuotaAux=totalQuotaAux.add(insurance);
		if (this.insuranceMortgage!=null)
			totalQuotaAux=totalQuotaAux.add(insuranceMortgage);
		return totalQuotaAux;
	}
	
	public BigDecimal getTotalDividend() {
		BigDecimal dividend = BigDecimal.ZERO;
		if (this.capital!=null)
			dividend=dividend.add(capital);
		if (this.interest!=null)
			dividend=dividend.add(interest);
		return dividend;
	}
}