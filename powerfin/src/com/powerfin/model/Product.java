package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.types.*;

/**
 * The persistent class for the product database table.
 * 
 */
@Entity
@Table(name = "product")
@Views({
		@View(members = "productClass; " + "productType; " + "productId; "
				+ "name; " + "currency;"
				+ "interestRate, defaultInterestRate; " + "singleAccount;"
				+ "ownProduct; "
				+ "autoCode[prefix, lpad, sequenceDBName, rpad, sufix]; "
				+ "categoryProducts; "
				+ "productStatuses"),
		@View(name = "Reference", members = "#productId, currency;"
				+ "productClass; " + "productType; " + "name;"),
		@View(name = "Simple", members = "#productId,name,currency;")})
@Tab(properties="productId,name,currency.currencyId,singleAccount,ownProduct, productType.name")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "product_id", unique = true, nullable = false, length = 20)
	private String productId;

	@Column(name = "default_interest_rate", precision = 5, scale = 2)
	private BigDecimal defaultInterestRate;

	@Column(nullable = false, length = 100)
	@Required
	@DisplaySize(30)
	private String name;

	@Column(length = 5)
	private String prefix;

	@Column(name = "interest_rate", precision = 5, scale = 2)
	private BigDecimal interestRate;

	@Column(name = "sequence_db_name")
	@DisplaySize(30)
	private String sequenceDBName;

	@Column(name = "single_account", nullable = false)
	@Required
	private Types.YesNoIntegerType singleAccount;

	@Column(name = "own_product", nullable = false)
	@Required
	private Types.YesNoIntegerType ownProduct;

	@Column(length = 5)
	private String sufix;

	@Column(name = "lpad", length = 10)
	private String lpad;

	@Column(name = "rpad", length = 10)
	private String rpad;

	// bi-directional many-to-one association to CategoryProduct
	@OneToMany(mappedBy = "product")
	@ReadOnly
	@ListProperties("category.categoryId, category.name, product.productId, product.productType.name, product.name, bookAccount, bookAccountName")
	private List<CategoryProduct> categoryProducts;

	// bi-directional many-to-one association to CategoryProduct
	@OneToMany(mappedBy = "product")
	@ReadOnly
	@ListProperties("accountStatus.name, byDefault")
	private List<ProductStatus> productStatuses;
	
	// bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name = "currency_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "currencyId")
	private Currency currency;

	// bi-directional many-to-one association to ProductType
	@ManyToOne
	@JoinColumn(name = "product_type_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name", depends = "this.productClass", condition = "${productClass.productClassId} = ?")
	private ProductType productType;

	// Transient

	// bi-directional many-to-one association to ProductType
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private ProductClass productClass;

	public Product() {
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public BigDecimal getDefaultInterestRate() {
		return defaultInterestRate;
	}

	public void setDefaultInterestRate(BigDecimal defaultInterestRate) {
		this.defaultInterestRate = defaultInterestRate;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getSequenceDBName() {
		return sequenceDBName;
	}

	public void setSequenceDBName(String sequenceDBName) {
		this.sequenceDBName = sequenceDBName;
	}

	public Types.YesNoIntegerType getSingleAccount() {
		return singleAccount;
	}

	public void setSingleAccount(Types.YesNoIntegerType singleAccount) {
		this.singleAccount = singleAccount;
	}

	public String getSufix() {
		return this.sufix;
	}

	public void setSufix(String sufix) {
		this.sufix = sufix;
	}

	public List<CategoryProduct> getCategoryProducts() {
		return this.categoryProducts;
	}

	public void setCategoryProducts(List<CategoryProduct> categoryProducts) {
		this.categoryProducts = categoryProducts;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public ProductType getProductType() {
		return this.productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductClass getProductClass() {
		return productType.getProductClass();
	}

	public void setProductClass(ProductClass productClass) {
		this.productClass = productClass;
	}

	public Types.YesNoIntegerType getOwnProduct() {
		return ownProduct;
	}

	public void setOwnProduct(Types.YesNoIntegerType ownProduct) {
		this.ownProduct = ownProduct;
	}

	public String getLpad() {
		return lpad;
	}

	public void setLpad(String lpad) {
		this.lpad = lpad;
	}

	public String getRpad() {
		return rpad;
	}

	public void setRpad(String rpad) {
		this.rpad = rpad;
	}

	public List<ProductStatus> getProductStatuses() {
		return productStatuses;
	}

	public void setProductStatuses(List<ProductStatus> productStatuses) {
		this.productStatuses = productStatuses;
	}
	
}