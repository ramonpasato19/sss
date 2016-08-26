package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.types.*;


/**
 * The persistent class for the product_type_status database table.
 * 
 */
@Entity
@Table(name="product_type_status")
@View(members="productType; accountStatus; byDefault")
@Tab(properties="productType.name, accountStatus.name, byDefault")
public class ProductTypeStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_type_status_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String productTypeStatusId;

	@Column(name="by_default", nullable=false)
	@Required
	private Types.YesNoIntegerType byDefault;

	//bi-directional many-to-one association to AccountStatus
	@ManyToOne
	@JoinColumn(name="account_status_id", nullable=false)
	@DescriptionsList
	@Required
	private AccountStatus accountStatus;

	//bi-directional many-to-one association to Product
	@ManyToOne
	@JoinColumn(name="product_type_id", nullable=false)
	@Required
	@DescriptionsList
	private ProductType productType;

	public ProductTypeStatus() {
	}

	public String getProductTypeStatusId() {
		return this.productTypeStatusId;
	}

	public void setProductTypeStatusId(String productTypeStatusId) {
		this.productTypeStatusId = productTypeStatusId;
	}

	public Types.YesNoIntegerType getByDefault() {
		return byDefault;
	}

	public void setByDefault(Types.YesNoIntegerType byDefault) {
		this.byDefault = byDefault;
	}

	public AccountStatus getAccountStatus() {
		return this.accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public ProductType getProductType() {
		return this.productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

}