package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.model.types.*;


/**
 * The persistent class for the category_product database table.
 * 
 */
@Entity
@Table(name="category_product")
@View(members="category;"
		+ "product;"
		+ "bookAccount;"
		+ "bookAccountId;"
		+ "allowsNegativeBalance")
@Tab(properties="category.categoryId, category.name, product.productId,product.name, bookAccount, bookAccountName")
public class CategoryProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(generator="system-uuid") 
	@Hidden
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="category_product_id", unique=true, nullable=false, length=32)
	private String categoryProductId;

	@Column(name="book_account", nullable=false, length=50)
	@Required
	@DisplaySize(20)
	private String bookAccount;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@NoFrame
	@ReferenceView("Reference")
	private Category category;

	//bi-directional many-to-one association to Product
	@ManyToOne
	@JoinColumn(name="product_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Product product;

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
	
	public CategoryProduct() {
	}

	public String getCategoryProductId() {
		return this.categoryProductId;
	}

	public void setCategoryProductId(String categoryProductId) {
		this.categoryProductId = categoryProductId;
	}

	public String getBookAccount() {
		return this.bookAccount;
	}

	public void setBookAccount(String bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
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
	
}