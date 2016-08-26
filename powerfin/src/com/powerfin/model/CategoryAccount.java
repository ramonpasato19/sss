package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.model.types.*;


/**
 * The persistent class for the category_account database table.
 * 
 */
@Entity
@Table(name="category_account")
@View(members="category;"
		+ "account;"
		+ "bookAccount;"
		+ "bookAccountId;"
		+ "allowsNegativeBalance")
@Tab(properties="category.categoryId, category.name, account.product.name, account.accountId, account.code, account.name, bookAccount, bookAccountName")
public class CategoryAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(generator="system-uuid") 
	@Hidden
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="category_account_id", unique=true, nullable=false, length=32)
	private String categoryAccountId;

	@Column(name="book_account", nullable=false, length=50)
	@Required
	@DisplaySize(20)
	private String bookAccount;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("reference")
	private Account account;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@NoFrame
	@ReferenceView("Reference")
	private Category category;
	
	@Column(name="allows_negative_balance", nullable=false)
	@Required
	private Types.YesNoIntegerType allowsNegativeBalance;

	//bi-directional many-to-one association to BookAccount
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchBookAccount.SearchMovementBookAccount")
	private BookAccount bookAccountId;
	
	public CategoryAccount() {
	}

	public String getCategoryAccountId() {
		return this.categoryAccountId;
	}

	public void setCategoryAccountId(String categoryAccountId) {
		this.categoryAccountId = categoryAccountId;
	}

	public String getBookAccount() {
		return this.bookAccount;
	}

	public void setBookAccount(String bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public BookAccount getBookAccountId() {
		return bookAccountId;
	}

	public void setBookAccountId(BookAccount bookAccountId) {
		this.bookAccountId = bookAccountId;
	}
	
	public String getBookAccountName()
	{
		BookAccount ba = (BookAccount)XPersistence.getManager().find(BookAccount.class, bookAccount);
		if (ba!=null)
			return ba.getName();
		else
			return "NO DEFINIDO";
	}

	public Types.YesNoIntegerType getAllowsNegativeBalance() {
		return allowsNegativeBalance;
	}

	public void setAllowsNegativeBalance(Types.YesNoIntegerType allowsNegativeBalance) {
		this.allowsNegativeBalance = allowsNegativeBalance;
	}

	@PreCreate
	public void onCreate()
	{
		if (allowsNegativeBalance==null)
			allowsNegativeBalance = Types.YesNoIntegerType.NO;
	}
}