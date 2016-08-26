package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the marital_status database table.
 * 
 */
@Entity
@Table(name="marital_status")
@NamedQuery(name="MaritalStatus.findAll", query="SELECT m FROM MaritalStatus m")
public class MaritalStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="marital_status_id", unique=true, nullable=false, length=3)
	private String maritalStatusId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="maritalStatus")
	private List<NaturalPerson> naturalPersons;

	public MaritalStatus() {
	}

	public String getMaritalStatusId() {
		return this.maritalStatusId;
	}

	public void setMaritalStatusId(String maritalStatusId) {
		this.maritalStatusId = maritalStatusId;
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
		naturalPerson.setMaritalStatus(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setMaritalStatus(null);

		return naturalPerson;
	}

}