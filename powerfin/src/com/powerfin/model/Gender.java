package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the gender database table.
 * 
 */
@Entity
@Table(name="gender")
@NamedQuery(name="Gender.findAll", query="SELECT g FROM Gender g")
public class Gender implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="gender_id", unique=true, nullable=false, length=1)
	private String genderId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="gender")
	private List<NaturalPerson> naturalPersons;

	public Gender() {
	}

	public String getGenderId() {
		return this.genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
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
		naturalPerson.setGender(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setGender(null);

		return naturalPerson;
	}

}