package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.types.*;


/**
 * The persistent class for the product_status database table.
 * 
 */
@Entity
@Table(name="product_status")
@View(members="product; accountStatus; byDefault")
@Tab(properties="product.name, accountStatus.name, byDefault")
public class ProductStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_status_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String productStatusId;

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
	@JoinColumn(name="product_id", nullable=false)
	@Required
	@DescriptionsList
	private Product product;

	public ProductStatus() {
	}

	public String getProductStatusId() {
		return productStatusId;
	}

	public void setProductStatusId(String productStatusId) {
		this.productStatusId = productStatusId;
	}

	public Types.YesNoIntegerType getByDefault() {
		return byDefault;
	}

	public void setByDefault(Types.YesNoIntegerType byDefault) {
		this.byDefault = byDefault;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}