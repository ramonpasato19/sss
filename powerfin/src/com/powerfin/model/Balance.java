package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;


/**
 * The persistent class for the balance database table.
 * 
 */
@Entity
@Table(name="balance")
public class Balance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "balance_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String balanceId;

	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", unique=true, nullable=false)
	private Date accountingDate;
	
	@Column(unique=true, nullable=false)
	private Integer subaccount;
	
	@Column(name="balance", nullable=false, precision=19, scale=2)
	private BigDecimal balance;

	@Column(name="official_balance", nullable=false, precision=19, scale=2)
	private BigDecimal officialBalance;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private Account account;

	//bi-directional many-to-one association to BookAccount
	@ManyToOne
	@JoinColumn(name="book_account_id", nullable=false)
	private BookAccount bookAccount;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	private Category category;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="currency_id", nullable=false)
	private Currency currency;
	
	@Temporal(TemporalType.DATE)
	@Column(name="from_date", unique=true, nullable=false)
	private Date fromDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="to_date", unique=true, nullable=false)
	private Date toDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date", unique=true, nullable=true)
	private Date dueDate;
	
	@Column(name="stock", precision=13, scale=4)
	private BigDecimal stock;

	public Balance() {
	}

	public String getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(String balanceId) {
		this.balanceId = balanceId;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Integer getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getBalance() {
		return balance==null?BigDecimal.ZERO:this.balance;
		
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getOfficialBalance() {
		return officialBalance==null?BigDecimal.ZERO:this.officialBalance;
	}

	public void setOfficialBalance(BigDecimal officialBalance) {
		this.officialBalance = officialBalance;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BookAccount getBookAccount() {
		return bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
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

}