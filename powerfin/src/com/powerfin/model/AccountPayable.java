package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_playable database table.
 * 
 */
@Entity
@Table(name="account_payable")
@Views({
	@View(members="account;"),
	@View(name="NewAccountPayable", members="accountId;"
			+ "person;"
			+ "product;"
		),
	@View(name="AccountPayableList", members="accountId;"
			+ "person;"
			+ "product"),
})
@Tabs({
	@Tab(properties="account.accountId, account.currency.currencyId, account.code, account.person.name, account.product.name, account.product.productType.name"),
	@Tab(name="AccountPayableList", properties="account.accountId, account.currency.currencyId, account.product.productId, account.product.name, account.person.name, account.balance, account.advanceBalance"),
	@Tab(name="NewAccountPayable", properties="account.accountId, account.currency.currencyId, account.product.productId, account.product.name, account.person.name"),
})
public class AccountPayable extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@DisplaySize(20)
	@ReadOnly
	private String accountId;
	
	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction(value="SearchProduct.SearchShareholderAndPayableProduct")
	private Product product;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Person person;

	@Transient
	@DisplaySize(20)
	@ReadOnly
	private String accountIdAux;
	
	public AccountPayable() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Product getProduct() {
		if (account!=null)
			return account.getProduct();
		else return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Person getPerson() {
		if (account!=null)
			return account.getPerson();
		else return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getAccountIdAux() {
		if (account!=null)
			return account.getAccountId();
		else return accountIdAux;
		
	}

	public void setAccountIdAux(String accountIdAux) {
		this.accountIdAux = accountIdAux;
	}

	public BigDecimal getBalance() throws Exception
	{
		return account.getBalance();
	}

}