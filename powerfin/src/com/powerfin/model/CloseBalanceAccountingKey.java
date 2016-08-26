package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the balance_accounting database table.
 * 
 */
@Embeddable
public class CloseBalanceAccountingKey implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", unique=true, nullable=false)
	private java.util.Date accountingDate;

	@Column(name="book_account_id", insertable=false, updatable=false, unique=true, nullable=false, length=50)
	private String bookAccountId;

	public CloseBalanceAccountingKey() {
	}
	public java.util.Date getAccountingDate() {
		return this.accountingDate;
	}
	public void setAccountingDate(java.util.Date accountingDate) {
		this.accountingDate = accountingDate;
	}
	public String getBookAccountId() {
		return this.bookAccountId;
	}
	public void setBookAccountId(String bookAccountId) {
		this.bookAccountId = bookAccountId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CloseBalanceAccountingKey)) {
			return false;
		}
		CloseBalanceAccountingKey castOther = (CloseBalanceAccountingKey)other;
		return 
			this.accountingDate.equals(castOther.accountingDate)
			&& this.bookAccountId.equals(castOther.bookAccountId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.accountingDate.hashCode();
		hash = hash * prime + this.bookAccountId.hashCode();
		
		return hash;
	}
}