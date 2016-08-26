package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;

import com.powerfin.model.types.Types.*;

import java.util.List;


/**
 * The persistent class for the group_account database table.
 * 
 */
@Entity
@Table(name="group_account")
@View(members="groupAccountId; "
		+ "name; "
		+ "debtorOrCreditor; "
		+ "bookAccounts")
public class GroupAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="group_account_id", unique=true, nullable=false, length=2)
	private String groupAccountId;

	@Type(type="org.openxava.types.EnumStringType",
			   parameters={
				@Parameter(name="strings", value="D,C"), // These are the values stored on the database
				@Parameter(name="enumType", value="com.powerfin.model.types.Types$DebitOrCredit")
			   }
		 )
	@Column(name="debtor_or_creditor", nullable=false, length=1)
	@Required
	private DebitOrCredit debtorOrCreditor;

	@Column(nullable=false, length=100)
	@Required
	private String name;

	//bi-directional many-to-one association to BookAccount
	@OneToMany(mappedBy="groupAccount")
	@ReadOnly
	private List<BookAccount> bookAccounts;

	public GroupAccount() {
	}

	public String getGroupAccountId() {
		return this.groupAccountId;
	}

	public void setGroupAccountId(String groupAccountId) {
		this.groupAccountId = groupAccountId;
	}

	public DebitOrCredit getDebtorOrCreditor() {
		return debtorOrCreditor;
	}

	public void setDebtorOrCreditor(DebitOrCredit debtorOrCreditor) {
		this.debtorOrCreditor = debtorOrCreditor;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BookAccount> getBookAccounts() {
		return this.bookAccounts;
	}

	public void setBookAccounts(List<BookAccount> bookAccounts) {
		this.bookAccounts = bookAccounts;
	}

	public BookAccount addBookAccount(BookAccount bookAccount) {
		getBookAccounts().add(bookAccount);
		bookAccount.setGroupAccount(this);

		return bookAccount;
	}

	public BookAccount removeBookAccount(BookAccount bookAccount) {
		getBookAccounts().remove(bookAccount);
		bookAccount.setGroupAccount(null);

		return bookAccount;
	}

}