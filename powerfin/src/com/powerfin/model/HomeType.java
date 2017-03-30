package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the home_type database table.
 * 
 */
@Entity
@Table(name="home_type")
@View(members = "homeTypeId;"
		+ "name;")
public class HomeType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="home_type_id", unique=true, nullable=false, length=3)
	private String homeTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="homeType")
	private List<NaturalPerson> naturalPersons;

	public HomeType() {
	}

	public String getHomeTypeId() {
		return this.homeTypeId;
	}

	public void setHomeTypeId(String homeTypeId) {
		this.homeTypeId = homeTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NaturalPerson> getNaturalPersons() {
		return this.naturalPersons;
	}

	public void setNaturalPersons(List<NaturalPerson> naturalPersons) {
		this.naturalPersons = naturalPersons;
	}

	public NaturalPerson addNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().add(naturalPerson);
		naturalPerson.setHomeType(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setHomeType(null);

		return naturalPerson;
	}

}