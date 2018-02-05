package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the category_type database table.
 * 
 */
@Entity
@Table(name="category_type")
@Views({
	@View(members="categoryTypeId; "
			+ "name;")
})
public class CategoryType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="category_type_id", unique=true, nullable=false, length=3)
	private String categoryTypeId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public CategoryType() {
	}

	public String getCategoryTypeId() {
		return categoryTypeId;
	}

	public void setCategoryTypeId(String categoryTypeId) {
		this.categoryTypeId = categoryTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}