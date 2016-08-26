package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the nationality database table.
 * 
 */
@Entity
@Table(name="nationality")
@NamedQuery(name="Nationality.findAll", query="SELECT n FROM Nationality n")
public class Nationality implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="nationality_id", unique=true, nullable=false, length=3)
	private String nationalityId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="nationality")
	private List<NaturalPerson> naturalPersons;

	public Nationality() {
	}

	public String getNationalityId() {
		return this.nationalityId;
	}

	public void setNationalityId(String nationalityId) {
		this.nationalityId = nationalityId;
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
		naturalPerson.setNationality(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setNationality(null);

		return naturalPerson;
	}

}