package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * The persistent class for the tax database table.
 * 
 */
@Entity
@Table(name = "tax")
@Views({
@View(members="taxId;"
		+ "name;"
		+ "percentage;"
		+ "taxType;"
		+ "externalCode;"
		+ "externalPercentageCode;"
		+ "description;"
		+ "category"),
@View(name="Simple", members="taxId,"
		+ "name;"
		+ "percentage;"),
@View(name="accountItemTax", members="taxId,"
		+ "name;"
		+ "percentage;")
})
@Tab(properties="taxId, name, percentage, taxType.name, category.categoryId")
public class Tax implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "tax_id", unique = true, nullable = false, length = 10)
	private String taxId;

	@Column(length = 400)
	private String description;

	@Column(nullable = false, length = 100)
	@DisplaySize(30)
	private String name;

	@Column(name = "external_code", length = 10)
	private String externalCode;

	@Column(name = "external_percentage_code", length = 10)
	private String externalPercentageCode;

	@Column(nullable = false, precision = 7, scale = 2)
	private BigDecimal percentage;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_type_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private TaxType taxType;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=true)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Category category;
	
	public Tax() {
	}

	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPercentage() {
		return this.percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getExternalPercentageCode() {
		return externalPercentageCode;
	}

	public void setExternalPercentageCode(String externalPercentageCode) {
		this.externalPercentageCode = externalPercentageCode;
	}

	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}