package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_shareholder database table.
 * 
 */
@Entity
@Table(name="account_shareholder")
@View(members="account;"
		+ "percentageParticipation")
@Tab(properties="account.accountId, account.person.name, percentageParticipation")
public class AccountShareholder extends AuditEntity implements Serializable {
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
	
	@Column(name="percentage_participation", precision=5, scale=2)
	private BigDecimal percentageParticipation;
		
	public AccountShareholder() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getPercentageParticipation() {
		return percentageParticipation;
	}

	public void setPercentageParticipation(BigDecimal percentageParticipation) {
		this.percentageParticipation = percentageParticipation;
	}

}