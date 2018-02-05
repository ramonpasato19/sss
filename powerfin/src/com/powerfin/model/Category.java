package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.model.types.*;


/**
 * The persistent class for the category database table.
 * 
 */
@Entity
@Table(name="category")
@Views({
	@View(members="categoryId; "
			+ "name;"
			+ "bookAccount;"
			+ "bookAccountId;"
			+ "allowsNegativeBalance;"
			+ "expiresZeroBalance;"
			+ "printable;"
			+ "categoryType"),
	@View(name="Reference", members="categoryId, name;")
})
@Tab(properties="categoryId, name, bookAccount, bookAccountName, categoryType.name, printable")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="category_id", unique=true, nullable=false, length=10)
	private String categoryId;

	@Column(nullable=false, length=100)
	@DisplaySize(50)
	@Required
	private String name;

	@Column(name="book_account", nullable=false, length=50)
	@Required
	@DisplaySize(20)
	private String bookAccount;
	
	//bi-directional many-to-one association to BookAccount
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchBookAccount.SearchMovementBookAccount")
	private BookAccount bookAccountId;
	
	@Column(name="allows_negative_balance", nullable=false)
	@Required
	private Types.YesNoIntegerType allowsNegativeBalance;
	
	@Column(name="expires_zero_balance", nullable=false)
	@Required
	private Types.YesNoIntegerType expiresZeroBalance;
	
	@Column(name="printable", nullable=false)
	@Required
	private Types.YesNoIntegerType printable;
	
	// bi-directional many-to-one association to IdentificationType
	@ManyToOne
	@JoinColumn(name = "category_type_id", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	private CategoryType categoryType;
	
	public Category() {
	}

	public String getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBookAccount() {
		return bookAccount;
	}

	public void setBookAccount(String bookAccount) {
		this.bookAccount = bookAccount;
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
			return XavaResources.getString("not_defined");
	}

	public Types.YesNoIntegerType getAllowsNegativeBalance() {
		return allowsNegativeBalance;
	}

	public void setAllowsNegativeBalance(
			Types.YesNoIntegerType allowsNegativeBalance) {
		this.allowsNegativeBalance = allowsNegativeBalance;
	}

	public Types.YesNoIntegerType getExpiresZeroBalance() {
		return expiresZeroBalance;
	}

	public void setExpiresZeroBalance(Types.YesNoIntegerType expiresZeroBalance) {
		this.expiresZeroBalance = expiresZeroBalance;
	}

	public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}

	public Types.YesNoIntegerType getPrintable() {
		return printable;
	}

	public void setPrintable(Types.YesNoIntegerType printable) {
		this.printable = printable;
	}

}