package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_bank database table.
 * 
 */
@Entity
@Table(name="account_bank")
@Views({
	@View(members="account;"
			+ "finantialInstitution;"
			+ "accountBankType"),
	@View(name="NewAccountBank", members="accountId;"
			+ "product;"
			+ "code;"
			+ "name;"
			+ "finantialInstitution;"
			+ "accountBankType;"
			+ "bookAccount"
		),
	@View(name="reference", members="account;"
			+ "finantialInstitution;"
			+ "accountBankType")
})
@Tabs({
	@Tab(properties="account.accountId, account.code, finantialInstitution, accountBankType.name"),
	@Tab(name="NewAccountBank", properties="accountId, account.name, account.code, finantialInstitution, accountBankType.name")
})

public class AccountBank extends AuditEntity implements Serializable {
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
	@Required
	private BookAccount bookAccount;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction(value="SearchProduct.SearchBankProduct")
	private Product product;
	
	@Transient
	@Required
	private String code;
	
	@Transient
	@Required
	private String name;
	
	@Column(name="finantial_institution", length=100)
	@Required
	private String finantialInstitution;

	@Column(name="maximum_number_check")
	private Integer maximumNumberCheck;

	@Column(name="minimum_number_check")
	private Integer minimumNumberCheck;

	@ManyToOne
	@JoinColumn(name="account_bank_type_id", nullable=false)
	@DescriptionsList(descriptionProperties="name")
	@NoCreate
	@NoModify
	@Required
	private AccountBankType accountBankType;
	
	public AccountBank() {
	}

	public String getFinantialInstitution() {
		return this.finantialInstitution;
	}

	public void setFinantialInstitution(String finantialInstitution) {
		this.finantialInstitution = finantialInstitution;
	}

	public Integer getMaximumNumberCheck() {
		return this.maximumNumberCheck;
	}

	public void setMaximumNumberCheck(Integer maximumNumberCheck) {
		this.maximumNumberCheck = maximumNumberCheck;
	}

	public Integer getMinimumNumberCheck() {
		return this.minimumNumberCheck;
	}

	public void setMinimumNumberCheck(Integer minimumNumberCheck) {
		this.minimumNumberCheck = minimumNumberCheck;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountBankType getAccountBankType() {
		return accountBankType;
	}

	public void setAccountBankType(AccountBankType accountBankType) {
		this.accountBankType = accountBankType;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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

}