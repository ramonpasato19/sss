package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the city database table.
 * 
 */
@Entity
@Table(name="city")
@Views({
	@View(members = "country;"
			+ "region;"
			+ "state;"
			+ "cityId;"
			+ "code;"
			+ "name;"),
	@View(name="Reference", members = "country, region, state, name"),
	@View(name="BirthCity", members = "name; country, region, state")
})
@Tab(properties="country.name, state.name, name")
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="city_id", unique=true, nullable=false)
	private Integer cityId;

	@Column(length=20)
	private String code;

	@Column(nullable=false, length=100)
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
	@DescriptionsList(descriptionProperties="code,name", depends="this.country",condition="${country.countryId} = ?")
	@Required
	private Region region;
	
	//bi-directional many-to-one association to State
	@ManyToOne
	@JoinColumn(name="state_id", nullable=false)
	@DescriptionsList(descriptionProperties="code,name", depends="this.country, this.region",condition="${country.countryId} = ? and ${region.regionId} = ?")
	@Required
	private State state;

	//bi-directional many-to-one association to District
	@OneToMany(mappedBy="city")
	private List<District> districts;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="city")
	private List<NaturalPerson> naturalPersons;

	public City() {
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
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

	public List<District> getDistricts() {
		return this.districts;
	}

	public void setDistricts(List<District> districts) {
		this.districts = districts;
	}

	public District addDistrict(District district) {
		getDistricts().add(district);
		district.setCity(this);

		return district;
	}

	public District removeDistrict(District district) {
		getDistricts().remove(district);
		district.setCity(null);

		return district;
	}

	public List<NaturalPerson> getNaturalPersons() {
		return this.naturalPersons;
	}

	public void setNaturalPersons(List<NaturalPerson> naturalPersons) {
		this.naturalPersons = naturalPersons;
	}

	public NaturalPerson addNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().add(naturalPerson);
		naturalPerson.setCity(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setCity(null);

		return naturalPerson;
	}

}