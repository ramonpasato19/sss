package com.powerfin.model.dto;

import java.math.*;
import java.util.*;

import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class FinancialCategoryDTO {
	private Account account;
	private int subaccount;
	private Category category;
	private BookAccount bookAccount;
	private Date accountingDate;
	private Date dueDate;
	private BigDecimal value;
	private BigDecimal stock;
	private BigDecimal officialValue;
	private BigDecimal exchangeRate;
	private Types.YesNoIntegerType updateBalance;
	private Types.YesNoIntegerType isOfficialValue;
	private Types.YesNoIntegerType allowCurrencyAdjustment;
	private Branch branch;
	
	public FinancialCategoryDTO(Account account, 
			int subaccount,
			Category category, 
			BookAccount bookAccount, 
			Date accountingDate,
			BigDecimal exchangeRate, 
			Types.YesNoIntegerType updateBalance, 
			Types.YesNoIntegerType isOfficialValue,
			Types.YesNoIntegerType allowCurrencyAdjustment,
			Date dueDate,
			Branch branch) {
		super();
		this.account = account;
		this.subaccount = subaccount;
		this.category = category;
		this.bookAccount = bookAccount;
		this.accountingDate = accountingDate;
		this.value = BigDecimal.ZERO;
		this.stock=BigDecimal.ZERO;
		this.officialValue = BigDecimal.ZERO;
		this.exchangeRate = exchangeRate;
		this.updateBalance = updateBalance;
		this.isOfficialValue = isOfficialValue;
		this.allowCurrencyAdjustment = allowCurrencyAdjustment;
		this.dueDate = dueDate;
		this.branch = branch;
	}

	public FinancialCategoryDTO() {
		// TODO Auto-generated constructor stub
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(int subaccount) {
		this.subaccount = subaccount;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public BookAccount getBookAccount() {
		return bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getOfficialValue() {
		return officialValue;
	}

	public void setOfficialValue(BigDecimal officialValue) {
		this.officialValue = officialValue;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Types.YesNoIntegerType getUpdateBalance() {
		return updateBalance;
	}

	public void setUpdateBalance(Types.YesNoIntegerType updateBalance) {
		this.updateBalance = updateBalance;
	}

	public Types.YesNoIntegerType getIsOfficialValue() {
		return isOfficialValue;
	}

	public void setIsOfficialValue(Types.YesNoIntegerType isOfficialValue) {
		this.isOfficialValue = isOfficialValue;
	}

	public Types.YesNoIntegerType getAllowCurrencyAdjustment() {
		return allowCurrencyAdjustment;
	}

	public void setAllowCurrencyAdjustment(Types.YesNoIntegerType allowCurrencyAdjustment) {
		this.allowCurrencyAdjustment = allowCurrencyAdjustment;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getStock() {
		return stock;
	}

	public void setStock(BigDecimal stock) {
		this.stock = stock;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	
}
