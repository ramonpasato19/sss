package com.powerfin.model;

import java.math.*;
import java.util.*;

public class AccountLoanDividend {
	
	private Account account;
	private Integer subaccount;
	private Integer daysOverdue;;
	private BigDecimal capital;
	private BigDecimal defaultInterest;
	private BigDecimal capitalReduced;
	private BigDecimal commission;
	private Date dueDate;
	private BigDecimal insurance;
	private BigDecimal insuranceMortgage;
	private BigDecimal interest;
	private Date paymentDate;
	private Integer provisionDays;
	
	public AccountLoanDividend() {
		super();
	}
	
	public AccountLoanDividend(
			Account account,
			Integer subaccount,
			Date dueDate,
			Integer daysOverdue,
			BigDecimal capital, 
			BigDecimal interest,
			BigDecimal defaultInterest
			) {
		super();
		this.account = account;
		this.subaccount = subaccount;
		this.capital = capital;
		this.dueDate = dueDate;
		this.daysOverdue = daysOverdue;
		this.interest = interest;
		this.defaultInterest = defaultInterest;
	}
	
	public AccountLoanDividend(
			Account account,
			Integer subaccount,
			Date dueDate,
			Integer daysOverdue,
			BigDecimal capital, 
			BigDecimal interest, 
			BigDecimal commission, 			 
			BigDecimal insurance, 
			BigDecimal insuranceMortgage
			) {
		super();
		this.account = account;
		this.subaccount = subaccount;
		this.capital = capital;
		this.commission = commission;
		this.dueDate = dueDate;
		this.daysOverdue = daysOverdue;
		this.insurance = insurance;
		this.insuranceMortgage = insuranceMortgage;
		this.interest = interest;
	}

	public Account getAccount() {
		return account;
	}

	public Integer getDaysOverdue() {
		return daysOverdue;
	}

	public void setDaysOverdue(Integer daysOverdue) {
		this.daysOverdue = daysOverdue;
	}

	public void setAccount(Account account) {
		this.account = account;
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
	public Date getPaymentDate() {
		return paymentDate;
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

	public BigDecimal getDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(BigDecimal defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	
}
