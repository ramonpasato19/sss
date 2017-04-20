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
 * The persistent class for the product_class database table.
 * 
 */
@Entity
@Table(name="product_class")
@View(members="productClassId;name;productTypes")
public class ProductClass implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_class_id", unique=true, nullable=false, length=3)
	private String productClassId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to ProductType
	@OneToMany(mappedBy="productClass")
	@ReadOnly
	@ListProperties("productTypeId, name")
	private List<ProductType> productTypes;

	@Type(type="org.openxava.types.EnumStringType",
			   parameters={
				@Parameter(name="strings", value="D,C"), // These are the values stored on the database
				@Parameter(name="enumType", value="com.powerfin.model.types.Types$DebitOrCredit")
			   }
		 )
	@Column(name="debtor_or_creditor", nullable=false, length=1)
	@Required
	private DebitOrCredit debtorOrCreditor;
	
	public ProductClass() {
	}

	public String getProductClassId() {
		return this.productClassId;
	}

	public void setProductClassId(String productClassId) {
		this.productClassId = productClassId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProductType> getProductTypes() {
		return this.productTypes;
	}

	public void setProductTypes(List<ProductType> productTypes) {
		this.productTypes = productTypes;
	}

	public ProductType addProductType(ProductType productType) {
		getProductTypes().add(productType);
		productType.setProductClass(this);

		return productType;
	}

	public ProductType removeProductType(ProductType productType) {
		getProductTypes().remove(productType);
		productType.setProductClass(null);

		return productType;
	}

	public DebitOrCredit getDebtorOrCreditor() {
		return debtorOrCreditor;
	}

	public void setDebtorOrCreditor(DebitOrCredit debtorOrCreditor) {
		this.debtorOrCreditor = debtorOrCreditor;
	}

}