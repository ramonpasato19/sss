package com.powerfin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Entity
@Table(name="brand")
@Views({
		@View(name="Product", members="brandId, name"),
		@View(name="Simple", members="brandId, name,countryId")
})
public class Brand {
	@Id
	@Column(name="brand_id", length=4)
	private String brandId;

	@Column(name="name", length=100)
	private String name;

	@DescriptionsList
	@ManyToOne
	@JoinColumn(name="country_id")
	private Country countryId;

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountryId() {
		return countryId;
	}

	public void setCountryId(Country countryId) {
		this.countryId = countryId;
	}



}
