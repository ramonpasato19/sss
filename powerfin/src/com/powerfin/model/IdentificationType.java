package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the identification_type database table.
 * 
 */
@Entity
@Table(name="identification_type")
@NamedQuery(name="IdentificationType.findAll", query="SELECT i FROM IdentificationType i")
public class IdentificationType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="identification_type_id", unique=true, nullable=false, length=3)
	private String identificationTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to Person
	@OneToMany(mappedBy="identificationType")
	private List<Person> persons;

	public IdentificationType() {
	}

	public String getIdentificationTypeId() {
		return this.identificationTypeId;
	}

	public void setIdentificationTypeId(String identificationTypeId) {
		this.identificationTypeId = identificationTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Person> getPersons() {
		return this.persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public Person addPerson(Person person) {
		getPersons().add(person);
		person.setIdentificationType(this);

		return person;
	}

	public Person removePerson(Person person) {
		getPersons().remove(person);
		person.setIdentificationType(null);

		return person;
	}

}