package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;

import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;

/**
 * The persistent class for the product database table.
 * 
 */
@Entity
@Table(name = "product")
@Views({
		@View(members = "productClass; " + "productType; " + "productId; "
				+ "name; " + "currency;"
				+ "interestRate, defaultInterestType;" 
				+ "singleAccount;"
				+ "ownProduct; "
				+ "daysGrace, daysGraceCollectionFee; "
				+ "applyDefaultInterestAccrued; "
				+ "applyAutomaticDebit; "
				+ "operatingCondition; "
				+ "salePortfolioUtilityDistribution; "
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

	@Column(name="days_grace")
	private Integer daysGrace;

	@Column(name="days_grace_collection_fee")
	private Integer daysGraceCollectionFee;
	
	@Column(name="apply_default_interest_accrued")
	@Required
	private Types.YesNoIntegerType applyDefaultInterestAccrued;
	
	@Column(name="apply_automatic_debit")
	@Required
	private Types.YesNoIntegerType applyAutomaticDebit;
	
	@Column(nullable = false, length = 100)
	@Required
	@DisplaySize(30)
	private String name;

	@Type(type="org.openxava.types.EnumStringType",
			   parameters={
				@Parameter(name="strings", value="R,V"), // These are the values stored on the database
				@Parameter(name="enumType", value="com.powerfin.model.types.Types$RateValue")
			   }
		 )
	@Column(name = "default_interest_type", length = 1)
	@Required
	private RateValue defaultInterestType;

	@Column(length = 5)
	private String prefix;
	
	@Column(name = "sale_portfolio_utility_distribution", length = 10)
	private String salePortfolioUtilityDistribution;
	
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

	@ManyToOne
	@JoinColumn(name="operating_condition_id", nullable=false)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	private OperatingCondition operatingCondition;
	
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

	public RateValue getDefaultInterestType() {
		return defaultInterestType;
	}

	public void setDefaultInterestType(RateValue defaultInterestType) {
		this.defaultInterestType = defaultInterestType;
	}

	public Integer getDaysGrace() {
		return daysGrace;
	}

	public void setDaysGrace(Integer daysGrace) {
		this.daysGrace = daysGrace;
	}

	public Integer getDaysGraceCollectionFee() {
		return daysGraceCollectionFee;
	}

	public void setDaysGraceCollectionFee(Integer daysGraceCollectionFee) {
		this.daysGraceCollectionFee = daysGraceCollectionFee;
	}

	public Types.YesNoIntegerType getApplyDefaultInterestAccrued() {
		return applyDefaultInterestAccrued;
	}

	public void setApplyDefaultInterestAccrued(Types.YesNoIntegerType applyDefaultInterestAccrued) {
		this.applyDefaultInterestAccrued = applyDefaultInterestAccrued;
	}

	public OperatingCondition getOperatingCondition() {
		return operatingCondition;
	}

	public void setOperatingCondition(OperatingCondition operatingCondition) {
		this.operatingCondition = operatingCondition;
	}

	public Types.YesNoIntegerType getApplyAutomaticDebit() {
		return applyAutomaticDebit;
	}

	public void setApplyAutomaticDebit(Types.YesNoIntegerType applyAutomaticDebit) {
		this.applyAutomaticDebit = applyAutomaticDebit;
	}

	public String getSalePortfolioUtilityDistribution() {
		return salePortfolioUtilityDistribution;
	}

	public void setSalePortfolioUtilityDistribution(String salePortfolioUtilityDistribution) {
		this.salePortfolioUtilityDistribution = salePortfolioUtilityDistribution;
	}
	
}