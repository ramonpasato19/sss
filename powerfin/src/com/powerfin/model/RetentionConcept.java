package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the retention_concept database table.
 * 
 */
@Entity
@Table(name="retention_concept")
@View(members="retentionConceptId;name;percentage;typeRetention; category")
@Tab(properties="retentionConceptId, name, percentage, typeRetention, category.categoryId")
public class RetentionConcept implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="retention_concept_id", unique=true, nullable=false, length=10)
	private String retentionConceptId;

	@Column(nullable=false, length=100)
	private String name;

	@Column(name = "percentage", nullable = false, precision=5, scale=2)
	private BigDecimal percentage;

	@Column(name ="type_retention", nullable=false, length=4)
	private String typeRetention;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=true)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Category category;
		
	public RetentionConcept() {
	}

	public String getRetentionConceptId() {
		return retentionConceptId;
	}

	public void setRetentionConceptId(String retentionConceptId) {
		this.retentionConceptId = retentionConceptId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getTypeRetention() {
		return typeRetention;
	}

	public void setTypeRetention(String typeRetention) {
		this.typeRetention = typeRetention;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}