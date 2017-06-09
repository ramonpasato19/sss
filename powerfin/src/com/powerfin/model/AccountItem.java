package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;



/**
 * The persistent class for the account_item database table.
 * 
 */
@Entity
@Table(name="account_item")
@NamedQuery(name="AccountItem.findAll", query="SELECT a FROM AccountItem a")

@Views({
	@View(members="#accountId;"
					+ "code;"
					+ "alternateCode;"
					+ "name;"
					+ "description;"
					+ "keywords;"
					+ "retailPrice;"
					+ "product{product};"
					+ "detail {"
					+ "unitMeasureBean;"
					+ "countryId;"
					+ "daysTolerance;"
					+ "inventoried; brandId;"
					+ "cellar[ minimalQuantity, maximumQuantity];"
					+ "}"

					+ "prices{"
					+ "cost; price;taxPrice;"
					+ "averageValue;"
					+ "};"
					+ "taxes{accountItemTax};"
					+ "categories{accountItemAccountItemType}"
					+ "Imagen{picture;}"
					),
	@View(name="basic",members="#accountId;"
			+ "code;"
			+ "name;"
			+ "description;"
			)
})
@Tab(properties="registrationDate, accountId, account.alternateCode, code, name, description, retailPrice, countryId.name ")
public class AccountItem extends AuditEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private String accountId;

	@Required
	@Column(length=50)
	private String code;

	@Hidden
	@Column(nullable=false, precision=13, scale=4)
	private BigDecimal cost;

	@Required
	@Column(length=150)
	private String description;

	@Required
	@Column(nullable=false)
	private Types.YesNoIntegerType inventoried;

	@Required
	@Column(nullable=false, length=100)
	private String name;

	//@OnChange(AccountItemChangeValues.class)
	@Required
	@Column(nullable=false, precision=13, scale=4)
	private BigDecimal price;

	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	@DescriptionsList
	@ManyToOne
	@JoinColumn(name="unit_measure", nullable=false)
	@NoCreate
	@NoModify
	private UnitMeasure unitMeasureBean;


	@Column(name="tax_price", precision=19, scale=2)
	private BigDecimal taxPrice;

	@Required
	@Column(name="retail_price", precision=19, scale=2)
	private BigDecimal retailPrice;

	@Transient
	@Action(value = "")
	@Column(name="retail_price_aux", insertable=false)
	private BigDecimal retailPriceAux;

	@Required
	@Column(name="minimal_quantity", precision=19, scale=2)
	private BigDecimal minimalQuantity;

	@Required
	@Column(name="maximum_quantity", precision=19, scale=2)
	private BigDecimal maximumQuantity;

	@Stereotype("PHOTO")
	private byte [] picture;

	@Stereotype("IMAGES_GALLERY")
	@Column(name="more_picture")
	private String morePicture;

	@OneToMany(
			mappedBy="accountItem",
			cascade=CascadeType.ALL)
	@AsEmbedded
	@ListProperties("tax.taxId, tax.name,tax.percentage, expireDate")
	@CollectionView("addTaxItem")
	@NewAction(value="AccountItemActions.AccountItemPreSaveDetails")
	private List<AccountItemTax> accountItemTax;


	@OneToMany(
			mappedBy="accountItem",
			cascade=CascadeType.ALL)
	@AsEmbedded
	@ListProperties("accountItemType.accountItemTypeId ,accountItemType.name")
	@CollectionView("addTypeItem")
	@NewAction(value="AccountItemActions.AccountItemPreSaveDetails")
	private List<AccountItemAccountItemType> accountItemAccountItemType;

	@ManyToOne
	@JoinColumn(name="brand_id")
	@NoCreate
	@NoModify
	@ReferenceView("Product")
	private Brand brandId;

	@DescriptionsList
	@ManyToOne
	@JoinColumn(name="country_id")
	private Country countryId;


	@Transient
	@Column(name="alternative_code",length=40)
	private String alternativeCode;

	@Column(name="days_tolerance")
	private Integer daysTolerance;

	@Column(name="keywords", length=100)
	private String keywords;

	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchActions({
	@SearchAction(value="SearchProduct.SearchItemProducts")
	})
	private Product product;

	@Column(name="average_value", precision=13, scale=4)
	private BigDecimal averageValue;

	@Transient
	@Column(name="alternate_code")
	private String alternateCode;

	public AccountItem() {
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Types.YesNoIntegerType getInventoried() {
		return inventoried;
	}

	public void setInventoried(Types.YesNoIntegerType inventoried) {
		this.inventoried = inventoried;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}


	public UnitMeasure getUnitMeasureBean() {
		return this.unitMeasureBean;
	}

	public void setUnitMeasureBean(UnitMeasure unitMeasureBean) {
		this.unitMeasureBean = unitMeasureBean;
	}

	public BigDecimal getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}

	public BigDecimal getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(BigDecimal taxPrice) {
		this.taxPrice = taxPrice;
	}

	public BigDecimal getMinimalQuantity() {
		return minimalQuantity;
	}

	public void setMinimalQuantity(BigDecimal minimalQuantity) {
		this.minimalQuantity = minimalQuantity;
	}

	public BigDecimal getMaximumQuantity() {
		return maximumQuantity;
	}

	public void setMaximumQuantity(BigDecimal maximumQuantity) {
		this.maximumQuantity = maximumQuantity;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public Brand getBrandId() {
		return brandId;
	}

	public void setBrandId(Brand brandId) {
		this.brandId = brandId;
	}

	public Country getCountryId() {
		return countryId;
	}

	public void setCountryId(Country countryId) {
		this.countryId = countryId;
	}


	public String getAlternativeCode() {
		return alternativeCode;
	}

	public void setAlternativeCode(String alternativeCode) {
		this.alternativeCode = alternativeCode;
	}

	public BigDecimal getRetailPriceAux() {
		return retailPriceAux;
	}

	public void setRetailPriceAux(BigDecimal retailPriceAux) {
		this.retailPriceAux = retailPriceAux;
	}

	public Integer getDaysTolerance() {
		return daysTolerance;
	}

	public void setDaysTolerance(Integer daysTolerance) {
		this.daysTolerance = daysTolerance;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getMorePicture() {
		return morePicture;
	}

	public void setMorePicture(String morePicture) {
		this.morePicture = morePicture;
	}

	public List<AccountItemTax> getAccountItemTax() {
		return accountItemTax;
	}

	public void setAccountItemTax(List<AccountItemTax> accountItemTax) {
		this.accountItemTax = accountItemTax;
	}

	public List<AccountItemAccountItemType> getAccountItemAccountItemType() {
		return accountItemAccountItemType;
	}

	public void setAccountItemAccountItemType(List<AccountItemAccountItemType> accountItemAccountItemType) {
		this.accountItemAccountItemType = accountItemAccountItemType;
	}

	public Product getProduct() {
		if(account!=null)
			return account.getProduct();
		else
			return null;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public BigDecimal getAverageValue() {
		return averageValue;
	}

	public void setAverageValue(BigDecimal averageValue) {
		this.averageValue = averageValue;
	}

	public String getAlternateCode() {
		if(account!=null)
			return account.getAlternateCode();
		else
			return null;
	}

	public void setAlternateCode(String alternateCode) {
		this.alternateCode = alternateCode;
	}

}