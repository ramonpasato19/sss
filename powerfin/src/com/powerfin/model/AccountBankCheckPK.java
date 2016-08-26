package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the account_bank_check database table.
 * 
 */
@Embeddable
public class AccountBankCheckPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="account_id", insertable=false, updatable=false, unique=true, nullable=false)
	private String accountId;

	@Column(name="check_number", unique=true, nullable=false)
	private Integer checkNumber;

	public AccountBankCheckPK() {
	}
	public String getAccountId() {
		return this.accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Integer getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(Integer checkNumber) {
		this.checkNumber = checkNumber;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AccountBankCheckPK)) {
			return false;
		}
		AccountBankCheckPK castOther = (AccountBankCheckPK)other;
		return 
			this.accountId.equals(castOther.accountId)
			&& this.checkNumber.equals(castOther.checkNumber);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.accountId.hashCode();
		hash = hash * prime + this.checkNumber.hashCode();
		
		return hash;
	}
}