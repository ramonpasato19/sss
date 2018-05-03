package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.types.Types;


/**
 * The persistent class for the balance_accounting database table.
 * 
 */
@Entity
@Table(name="close_balance_accounting")
@IdClass(CloseBalanceAccountingKey.class)
@Tab(properties="accountingDate, bookAccount.bookAccountId, bookAccount.name, "
			+ "officialBalance, currency.currencyId, bookAccount.level")
public class CloseBalanceAccounting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", unique=true, nullable=false)
	private java.util.Date accountingDate;

	@Id
	@Column(name="book_account_id", insertable=false, updatable=false, unique=true, nullable=false, length=50)
	private String bookAccountId;
	
	@Column(precision=19, scale=2)
	private BigDecimal balance;

	@Column(name="official_balance", precision=19, scale=2)
	private BigDecimal officialBalance;

	//bi-directional many-to-one association to BookAccount
	@ManyToOne
	@JoinColumn(name="book_account_id", nullable=false, insertable=false, updatable=false)
	private BookAccount bookAccount;

	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="official_currency_id", nullable=false)
	private Currency officialCurrency;

	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="currency_id")
	private Currency currency;

	public CloseBalanceAccounting() {
	}

	public java.util.Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(java.util.Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public String getBookAccountId() {
		return bookAccountId;
	}

	public void setBookAccountId(String bookAccountId) {
		this.bookAccountId = bookAccountId;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getOfficialBalance() {
		return this.officialBalance;
	}

	public void setOfficialBalance(BigDecimal officialBalance) {
		this.officialBalance = officialBalance;
	}

	public BookAccount getBookAccount() {
		return this.bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Currency getOfficialCurrency() {
		return officialCurrency;
	}

	public void setOfficialCurrency(Currency officialCurrency) {
		this.officialCurrency = officialCurrency;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public BigDecimal getOfficialBalanceDebit() {
		if (bookAccount.getGroupAccount().getDebtorOrCreditor().equals(Types.DebitOrCredit.DEBIT))
			return this.officialBalance;
		else 
			return null;
	}

	public BigDecimal getOfficialBalanceCredit() {
		if (bookAccount.getGroupAccount().getDebtorOrCreditor().equals(Types.DebitOrCredit.CREDIT))
			return this.officialBalance;
		else 
			return null;
	}
	
	public BigDecimal getNoOfficialbalance()
	{
		if (currency!=null && !currency.getCurrencyId().equals(officialCurrency.getCurrencyId()))
			return balance;
		else
			return null;
	}
}