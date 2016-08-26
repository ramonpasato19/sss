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
	@AsEmbedded
	@NoFrame
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	@ReferenceView("Shareholder")
	private Account account;
	
	@Column(name="percentage_participation", precision=5, scale=2)
	private BigDecimal percentageParticipation;
		
	public AccountShareholder() {
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