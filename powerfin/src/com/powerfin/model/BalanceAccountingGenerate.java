package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="accountingDate;"
		+ "level;"
		+ "balances;"
		+ "resume[assetsValue;liabilitiesAndCapital]")
public class BalanceAccountingGenerate {

	@Column
	private Date accountingDate;
	
	@Column
	private Integer level;
	
	@ReadOnly
	private BigDecimal assetsValue;
	
	@ReadOnly
	private BigDecimal liabilitiesValue;
	
	@ReadOnly
	private BigDecimal capitalValue;
	
	@ReadOnly
	private BigDecimal incomeValue;
	
	@ReadOnly
	private BigDecimal expenseValue;
	
	@ReadOnly
	private BigDecimal liabilitiesAndCapital;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value="accountingDate, bookAccount.bookAccountId, bookAccount.name, "
			+ "officialBalance, noOfficialbalance, currency.currencyId, bookAccount.level")
	@ListActions({
		@ListAction("BalanceAccountingGenerateController.generatePdf"),
		@ListAction("BalanceAccountingGenerateController.generateExcel")
	})
	@OrderBy("bookAccount.bookAccountId")
	@Condition(value="${accountingDate} = ${this.accountingDate}")
	private List<BalanceAccounting> balances;

	public BalanceAccountingGenerate() {
		
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public List<BalanceAccounting> getBalances() {
		return balances;
	}

	public void setBalances(List<BalanceAccounting> balances) {
		this.balances = balances;
	}

	public BigDecimal getAssetsValue() {
		return assetsValue;
	}

	public void setAssetsValue(BigDecimal assetsValue) {
		this.assetsValue = assetsValue;
	}

	public BigDecimal getLiabilitiesValue() {
		return liabilitiesValue;
	}

	public void setLiabilitiesValue(BigDecimal liabilitiesValue) {
		this.liabilitiesValue = liabilitiesValue;
	}

	public BigDecimal getCapitalValue() {
		return capitalValue;
	}

	public void setCapitalValue(BigDecimal capitalValue) {
		this.capitalValue = capitalValue;
	}

	public BigDecimal getIncomeValue() {
		return incomeValue;
	}

	public void setIncomeValue(BigDecimal incomeValue) {
		this.incomeValue = incomeValue;
	}

	public BigDecimal getExpenseValue() {
		return expenseValue;
	}

	public void setExpenseValue(BigDecimal expenseValue) {
		this.expenseValue = expenseValue;
	}

	public BigDecimal getLiabilitiesAndCapital() {
		return liabilitiesAndCapital;
	}

	public void setLiabilitiesAndCapital(BigDecimal liabilitiesAndCapital) {
		this.liabilitiesAndCapital = liabilitiesAndCapital;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}	
	
}
