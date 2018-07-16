package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DisplaySize;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Tab;
import org.openxava.annotations.Tabs;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;


/**
 * The persistent class for the account_paytable database table.
 * 
 */
@Entity
@Table(name="account_paytable")
@Views({
		@View(members="#accountId; "
				+ "subaccount; "
				+ "dueDate;"
				+ "provisionDays;"
				+ "capitalReduced;"
				+ "capital, interest;"
				+ "insurance, insuranceMortgage;"
				+ "commission;"
				+ "purchaseSpread, utilitySalePortfolio;"
				+ "paymentDate, lastPaymentDate;"
				+ "lastPaymentDateDefaultInterest, lastPaymentDateCollection;"
				+ "account;"),
		@View(name="ConsultPaytable", 
				members="#accountId; "
				+ "subaccount; "
				+ "dueDate;"
				+ "provisionDays;"
				+ "capitalReduced;"
				+ "capital, interest;"
				+ "insurance, insuranceMortgage;"
				+ "commission;"
				+ "purchaseSpread, utilitySalePortfolio;"
				+ "paymentDate, lastPaymentDate;"
				+ "lastPaymentDateDefaultInterest, lastPaymentDateCollection;"
				+ "account;"),
		@View(name="UpdateAccountPaytableDates", 
				members="#accountId; "
				+ "subaccount; "
				+ "dueDate;"
				+ "provisionDays;"
				+ "capitalReduced;"
				+ "capital, interest;"
				+ "insurance, insuranceMortgage;"
				+ "commission;"
				+ "purchaseSpread, utilitySalePortfolio;"
				+ "paymentDate, lastPaymentDate;"
				+ "lastPaymentDateDefaultInterest, lastPaymentDateCollection;"
				+ "account;"),
		@View(name="UpdateAccountPaytableInsurances", 
				members="#accountId; "
				+ "subaccount; "
				+ "dueDate;"
				+ "provisionDays;"
				+ "capitalReduced;"
				+ "capital, interest;"
				+ "insurance, insuranceMortgage;"
				+ "commission;"
				+ "purchaseSpread, utilitySalePortfolio;"
				+ "paymentDate, lastPaymentDate;"
				+ "lastPaymentDateDefaultInterest, lastPaymentDateCollection;"
				+ "account;"),
		@View(name="UpdateAccountPaytable", 
				members="#accountId; "
				+ "subaccount; "
				+ "dueDate;"
				+ "provisionDays;"
				+ "capitalReduced;"
				+ "capital, interest;"
				+ "insurance, insuranceMortgage;"
				+ "commission;"
				+ "purchaseSpread, utilitySalePortfolio;"
				+ "paymentDate, lastPaymentDate;"
				+ "lastPaymentDateDefaultInterest, lastPaymentDateCollection;"
				+ "account;"),
})
@Tabs({
	@Tab(properties="accountId, account.accountStatus.accountStatusId, account.accountStatus.name, account.product.productId, account.product.name, subaccount, dueDate, provisionDays, capitalReduced, capital, interest, insurance, insuranceMortgage, commission, purchaseSpread, utilitySalePortfolio, paymentDate, lastPaymentDate, lastPaymentDateDefaultInterest, lastPaymentDateCollection"),
	@Tab(name="UpdateAccountPaytableDates", properties="accountId, account.accountStatus.accountStatusId, account.accountStatus.name, account.product.productId, account.product.name, subaccount, dueDate, provisionDays, capitalReduced, capital, interest, insurance, insuranceMortgage, commission, purchaseSpread, utilitySalePortfolio, paymentDate, lastPaymentDate, lastPaymentDateDefaultInterest, lastPaymentDateCollection"),
	@Tab(name="UpdateAccountPaytableInsurances", properties="accountId, account.accountStatus.accountStatusId, account.accountStatus.name, account.product.productId, account.product.name, subaccount, dueDate, provisionDays, capitalReduced, capital, interest, insurance, insuranceMortgage, commission, purchaseSpread, utilitySalePortfolio, paymentDate, lastPaymentDate, lastPaymentDateDefaultInterest, lastPaymentDateCollection"),
	@Tab(name="UpdateAccountPaytable", properties="accountId, account.accountStatus.accountStatusId, account.accountStatus.name, account.product.productId, account.product.name, subaccount, dueDate, provisionDays, capitalReduced, capital, interest, insurance, insuranceMortgage, commission, purchaseSpread, utilitySalePortfolio, paymentDate, lastPaymentDate, lastPaymentDateDefaultInterest, lastPaymentDateCollection")
})
public class AccountPaytable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_paytable_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountPaytableId;

	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly(notForViews="DEFAULT, UpdateAccountPaytable")
	@DisplaySize(25)
	private String accountId;
	
	@Column(unique=true, nullable=false)
	@ReadOnly(notForViews="DEFAULT, UpdateAccountPaytable")
	private Integer subaccount;
	
	@Column(precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal capital;

	@Column(name="capital_reduced", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal capitalReduced;

	@Column(precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal commission;

	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private Date dueDate;

	@Column(precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates")
	private BigDecimal insurance;

	@Column(name="insurance_mortgage", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates")
	private BigDecimal insuranceMortgage;

	@Column(precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal interest;

	@Column(name="purchase_spread", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal purchaseSpread;
	
	@Column(name="utility_sale_portfolio", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private BigDecimal utilitySalePortfolio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="payment_date")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableInsurances")
	private Date paymentDate;

	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableInsurances")
	private Date lastPaymentDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_collection")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableInsurances")
	private Date lastPaymentDateCollection;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_default_int")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableInsurances")
	private Date lastPaymentDateDefaultInterest;
	
	@Column(name="provision_days")
	@ReadOnly(forViews="ConsultPaytable, UpdateAccountPaytableDates, UpdateAccountPaytableInsurances")
	private Integer provisionDays;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	@ReferenceView("reference")
	@ReadOnly(notForViews="DEFAULT, UpdateAccountPaytable")
	private Account account;
		
	public AccountPaytable() {
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

	public String getAccountPaytableId() {
		return accountPaytableId;
	}

	public void setAccountPaytableId(String accountPaytableId) {
		this.accountPaytableId = accountPaytableId;
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