package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the product_type database table.
 * 
 */
@Entity
@Table(name="person_type")
@View(members="personTypeId;name")
public class PersonType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_type_id", unique=true, nullable=false, length=3)
	private String personTypeId;

	@Column(nullable=false, length=100)
	private String name;

	public PersonType() {
	}

	public String getPersonTypeId() {
		return personTypeId;
	}

	public void setPersonTypeId(String personTypeId) {
		this.personTypeId = personTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}