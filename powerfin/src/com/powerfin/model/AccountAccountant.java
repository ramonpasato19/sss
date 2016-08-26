package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_accountant database table.
 * 
 */
@Entity
@Table(name="account_accountant")
@Views({
	@View(members="accountId;"
				+ "product;"
				+ "code;"
				+ "name;"
				+ "transactionalName;"
				+ "bookAccount"
			),
	@View(name="NewAccountAccountant", members="accountId;"
			+ "product;"
			+ "code;"
			+ "name;"
			+ "transactionalName;"
			+ "bookAccount"
		),
})
@Tabs({
	@Tab(properties="accountId, account.code, account.name"),
	@Tab(name="NewAccountAccountant", properties="accountId, account.code, account.name")
})
public class AccountAccountant extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@Hidden
	//@ReadOnly
	@Required
	private String accountId;
	
	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	//bi-directional many-to-one association to BookAccount
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchBookAccount.SearchManualEntryBookAccount")
	private BookAccount bookAccount;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction(value="SearchProduct.SearchAccountAccountant")
	private Product product;
	
	@Transient
	@ReadOnly
	private String code;
	
	@Transient
	@ReadOnly
	private String name;
	
	@Transient
	@Required
	private String transactionalName;
	
	public AccountAccountant() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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

	public Product getProduct() {
		if (account!=null)
			return account.getProduct();
		else return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getCode() {
		if (account!=null)
			return account.getCode();
		else return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		if (account!=null)
			return account.getName();
		else return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransactionalName() {
		return transactionalName;
	}

	public void setTransactionalName(String transactionalName) {
		this.transactionalName = transactionalName;
	}

}