package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="account_status")
@Views({
	@View(members="accountStatusId; "
			+ "name;")
})
public class AccountStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_status_id", unique=true, nullable=false, length=10)
	private String accountStatusId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public AccountStatus() {
	}

	public String getAccountStatusId() {
		return this.accountStatusId;
	}

	public void setAccountStatusId(String accountStatusId) {
		this.accountStatusId = accountStatusId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}