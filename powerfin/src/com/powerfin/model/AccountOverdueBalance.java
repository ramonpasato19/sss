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
@View(members="accountId;subaccount;"
		+ "dueDate;"
		+ "overdueDays;"
		+ "realOverdueDays;"
		+ "capital;"
		+ "interest;"
		+ "insuranceMortgage;"
		+ "insurance;"
		+ "defaultInterest;"
		+ "collectionFee;"
		+ "legalFee;"
		+ "receivableFee;"
		+ "total;"
		+ "lastPaymentDate;"
		+ "lastPaymentDateCollection;"
		+ "lastPaymentDateDefaultInterest;"
		)
@Table(name = "account_overdue_balance")

public class AccountOverdueBalance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "account_overdue_balance_id", unique = true, nullable = false, length = 32)
	@Hidden
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountOverdueBalanceId;

	@Column(name = "account_id", unique = true, nullable = false)
	private String accountId;

	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", unique=true, nullable=false)
	private Date accountingDate;
	
	@Column(unique = true, nullable = false)
	private Integer subaccount;

	@Column(precision = 11, scale = 2)
	private BigDecimal capital;

	@Column(name = "capital_reduced", precision = 11, scale = 2)
	private BigDecimal capitalReduced;

	@Column(precision = 11, scale = 2)
	private BigDecimal commission;

	@Temporal(TemporalType.DATE)
	@Column(name = "due_date")
	private Date dueDate;

	@Column(precision = 11, scale = 2)
	private BigDecimal insurance;

	@Column(name = "insurance_mortgage", precision = 11, scale = 2)
	private BigDecimal insuranceMortgage;

	@Column(precision = 11, scale = 2)
	private BigDecimal interest;

	@Column(name = "collection_fee", precision = 11, scale = 2)
	private BigDecimal collectionFee;
	
	@Column(name = "receivable_fee", precision = 11, scale = 2)
	private BigDecimal receivableFee;
	
	@Column(name = "legal_fee", precision = 11, scale = 2)
	private BigDecimal legalFee;
	
	@Column(precision = 11, scale = 2)
	private BigDecimal total;

	@Column(name = "default_interest", precision = 11, scale = 2)
	private BigDecimal defaultInterest;

	@Column(name = "overdue_days")
	private Integer overdueDays;

	@Column(name = "real_overdue_days")
	private Integer realOverdueDays;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date")
	private Date lastPaymentDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_collection")
	private Date lastPaymentDateCollection;
	
	@Temporal(TemporalType.DATE)
	@Column(name="last_payment_date_default_int")
	private Date lastPaymentDateDefaultInterest;
	
	// bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false, insertable = false, updatable = false)
	private Account account;

	public AccountOverdueBalance() {
	}

	public String getAccountOverdueBalanceId() {
		return accountOverdueBalanceId;
	}

	public void setAccountOverdueBalanceId(String accountOverdueBalanceId) {
		this.accountOverdueBalanceId = accountOverdueBalanceId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Integer getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getCapitalReduced() {
		return capitalReduced;
	}

	public void setCapitalReduced(BigDecimal capitalReduced) {
		this.capitalReduced = capitalReduced;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getInsurance() {
		return insurance;
	}

	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}

	public BigDecimal getInsuranceMortgage() {
		return insuranceMortgage;
	}

	public void setInsuranceMortgage(BigDecimal insuranceMortgage) {
		this.insuranceMortgage = insuranceMortgage;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(BigDecimal defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	public Integer getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public BigDecimal getCollectionFee() {
		return collectionFee;
	}

	public void setCollectionFee(BigDecimal collectionFee) {
		this.collectionFee = collectionFee;
	}

	public BigDecimal getReceivableFee() {
		return receivableFee;
	}

	public void setReceivableFee(BigDecimal receivableFee) {
		this.receivableFee = receivableFee;
	}

	public BigDecimal getLegalFee() {
		return legalFee;
	}

	public void setLegalFee(BigDecimal legalFee) {
		this.legalFee = legalFee;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public Integer getRealOverdueDays() {
		return realOverdueDays;
	}

	public void setRealOverdueDays(Integer realOverdueDays) {
		this.realOverdueDays = realOverdueDays;
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
		if (this.capital != null)
			totalQuotaAux = totalQuotaAux.add(capital);
		if (this.interest != null)
			totalQuotaAux = totalQuotaAux.add(interest);
		if (this.insurance != null)
			totalQuotaAux = totalQuotaAux.add(insurance);
		if (this.insuranceMortgage != null)
			totalQuotaAux = totalQuotaAux.add(insuranceMortgage);
		if (this.collectionFee != null)
			totalQuotaAux = totalQuotaAux.add(collectionFee);
		if (this.receivableFee != null)
			totalQuotaAux = totalQuotaAux.add(receivableFee);
		if (this.legalFee != null)
			totalQuotaAux = totalQuotaAux.add(legalFee);
		return totalQuotaAux;
	}

	public BigDecimal getTotalDividend() {
		BigDecimal dividend = BigDecimal.ZERO;
		if (this.capital != null)
			dividend = dividend.add(capital);
		if (this.interest != null)
			dividend = dividend.add(interest);
		return dividend;
	}
}