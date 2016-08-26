package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the region database table.
 * 
 */
@Entity
@Table(name="region")
@NamedQuery(name="Region.findAll", query="SELECT r FROM Region r")
public class Region implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="region_id", unique=true, nullable=false)
	private Integer regionId;

	@Column(length=4)
	private String code;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to City
	@OneToMany(mappedBy="region")
	private List<City> cities;

	//bi-directional many-to-one association to District
	@OneToMany(mappedBy="region")
	private List<District> districts;

	//bi-directional many-to-one association to Country
	@ManyToOne
	@JoinColumn(name="country_id", nullable=false)
	private Country country;

	//bi-directional many-to-one association to State
	@OneToMany(mappedBy="region")
	private List<State> states;

	public Region() {
	}

	public Integer getRegionId() {
		return this.regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
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

	public List<City> getCities() {
		return this.cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public City addCity(City city) {
		getCities().add(city);
		city.setRegion(this);

		return city;
	}

	public City removeCity(City city) {
		getCities().remove(city);
		city.setRegion(null);

		return city;
	}

	public List<District> getDistricts() {
		return this.districts;
	}

	public void setDistricts(List<District> districts) {
		this.districts = districts;
	}

	public District addDistrict(District district) {
		getDistricts().add(district);
		district.setRegion(this);

		return district;
	}

	public District removeDistrict(District district) {
		getDistricts().remove(district);
		district.setRegion(null);

		return district;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<State> getStates() {
		return this.states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public State addState(State state) {
		getStates().add(state);
		state.setRegion(this);

		return state;
	}

	public State removeState(State state) {
		getStates().remove(state);
		state.setRegion(null);

		return state;
	}

}