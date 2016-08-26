package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;

import org.openxava.annotations.*;

import java.util.List;


/**
 * The persistent class for the product_type database table.
 * 
 */
@Entity
@Table(name="product_type")
@View(members="productClass;productTypeId;name;products")
public class ProductType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_type_id", unique=true, nullable=false, length=3)
	private String productTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to Product
	@OneToMany(mappedBy="productType")
	@ReadOnly
	@ListProperties("productId, name")
	private List<Product> products;

	//bi-directional many-to-one association to ProductClass
	@ManyToOne
	@JoinColumn(name="product_class_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="name")
	private ProductClass productClass;

	public ProductType() {
	}

	public String getProductTypeId() {
		return this.productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Product addProduct(Product product) {
		getProducts().add(product);
		product.setProductType(this);

		return product;
	}

	public Product removeProduct(Product product) {
		getProducts().remove(product);
		product.setProductType(null);

		return product;
	}

	public ProductClass getProductClass() {
		return this.productClass;
	}

	public void setProductClass(ProductClass productClass) {
		this.productClass = productClass;
	}

}