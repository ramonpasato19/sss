package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="account_bank_type")
@Views({
	@View(members="accountBankTypeId; "
			+ "name;")
})
public class AccountBankType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_bank_type_id", unique=true, nullable=false, length=3)
	private String accountBankTypeId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public AccountBankType() {
	}

	public String getAccountBankTypeId() {
		return accountBankTypeId;
	}

	public void setAccountBankTypeId(String accountBankTypeId) {
		this.accountBankTypeId = accountBankTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}