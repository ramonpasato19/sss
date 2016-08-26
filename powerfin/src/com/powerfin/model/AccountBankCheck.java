package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_bank_check database table.
 * 
 */
@Entity
@Table(name="account_bank_check")
@NamedQuery(name="AccountBankCheck.findAll", query="SELECT a FROM AccountBankCheck a")
public class AccountBankCheck extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AccountBankCheckPK id;

	@Column(length=100)
	private String detail;

	@Column(name="status_check", nullable=false, length=3)
	private String statusCheck;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	public AccountBankCheck() {
	}

	public AccountBankCheckPK getId() {
		return this.id;
	}

	public void setId(AccountBankCheckPK id) {
		this.id = id;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getStatusCheck() {
		return this.statusCheck;
	}

	public void setStatusCheck(String statusCheck) {
		this.statusCheck = statusCheck;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}