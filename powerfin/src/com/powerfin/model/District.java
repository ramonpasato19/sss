package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the district database table.
 * 
 */
@Entity
@Table(name="district")
@Views({
	@View(members = "country;" + "region;" + "state;" + "city;" + "districtId;"+ "code;" + "name;"),
	@View(name = "HomeDistrict", members = "districtId, name; country, region, state, city; "),
	@View(name = "WorkDistrict", members = "districtId, name; country, region, state, city; "),
})
@Tab(properties = "country.name, region.name, state.name, city.name, name")
public class District implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="district_id", unique=true, nullable=false)
	private Integer districtId;

	@Column(length=20)
	private String code;

	@Column(nullable=false, length=100)
	@DisplaySize(30)
	private String name;

	//bi-directional many-to-one association to Country
	@ManyToOne
	@JoinColumn(name="country_id", nullable=false)
	@DescriptionsList(descriptionProperties="countryId,name")
	@Required
	private Country country;

	//bi-directional many-to-one association to State
	@ManyToOne
	@JoinColumn(name="region_id", nullable=false)
	@DescriptionsList(descriptionProperties="name", depends="this.country",condition="${country.countryId} = ?")
	@Required
	private Region region;
	
	//bi-directional many-to-one association to State
	@ManyToOne
	@JoinColumn(name="state_id", nullable=false)
	@DescriptionsList(descriptionProperties="name", depends="this.country, this.region",condition="${country.countryId} = ? and ${region.regionId} = ?")
	@Required
	private State state;

	// bi-directional many-to-one association to State
	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", depends = "this.country, this.region, this.state", condition = "${country.countryId} = ? and ${region.regionId} = ? and ${state.stateId} = ?")
	@Required
	private City city;

	public District() {
	}

	public Integer getDistrictId() {
		return this.districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public City getCity() {
		return this.city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Region getRegion() {
		return this.region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}


}