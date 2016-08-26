package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;


/**
 * The persistent class for the transaction_module_account database table.
 * 
 */
@Entity
@Table(name="transaction_module_account")
@View(members="transactionModuleAccountId;currency;"
		+ "transactionModule;"
		+ "account;"
		+ "name")
@Tab(properties="transactionModule.transactionModuleId, name, account.accountId, account.name, account.currency.currencyId, transactionModule.name")
public class TransactionModuleAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="transaction_module_account_id", unique=true, nullable=false, length=32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@ReadOnly
	private String transactionModuleAccountId;

	@Column(name="name", length=100)
	@Required
	private String name;

	//bi-directional many-to-one association to TransactionModule
	@ManyToOne
	@JoinColumn(name="transaction_module_id", nullable=false)
	@ReferenceView(value="simple")
	@NoCreate
	@NoModify
	private TransactionModule transactionModule;

	//bi-directional many-to-one association to TransactionModule
	@ManyToOne
	@JoinColumn(name = "account_id")
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("simple")
	@SearchAction(value="SearchAccount.SearchAccountAccountant")
	private Account account;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="currencyId, name")
	private Currency currency;
	
	public TransactionModuleAccount() {
	}

	public String getTransactionModuleAccountId() {
		return transactionModuleAccountId;
	}

	public void setTransactionModuleAccountId(String transactionModuleAccountId) {
		this.transactionModuleAccountId = transactionModuleAccountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
}