package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_portfolio_status database table.
 * 
 */
@Entity
@Table(name="account_portfolio_status")
@Views({
	@View(members="accountPortfolioStatusId; "
			+ "name;")
})
public class AccountPortfolioStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_portfolio_status_id", unique=true, nullable=false, length=3)
	private String accountPortfolioStatusId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public AccountPortfolioStatus() {
	}

	public String getAccountPortfolioStatusId() {
		return accountPortfolioStatusId;
	}

	public void setAccountPortfolioStatusId(String accountPortfolioStatusId) {
		this.accountPortfolioStatusId = accountPortfolioStatusId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}