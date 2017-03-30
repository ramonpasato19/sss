package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the immovable_type database table.
 * 
 */
@Entity
@Table(name="immovable_type")
@View(members = "immovableTypeId;"
		+ "name;")
public class ImmovableType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="immovable_type_id", unique=true, nullable=false, length=3)
	private String immovableTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonImmovable
	@OneToMany(mappedBy="immovableType")
	private List<PersonImmovable> personImmovables;

	public ImmovableType() {
	}

	public String getImmovableTypeId() {
		return this.immovableTypeId;
	}

	public void setImmovableTypeId(String immovableTypeId) {
		this.immovableTypeId = immovableTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonImmovable> getPersonImmovables() {
		return this.personImmovables;
	}

	public void setPersonImmovables(List<PersonImmovable> personImmovables) {
		this.personImmovables = personImmovables;
	}

	public PersonImmovable addPersonImmovable(PersonImmovable personImmovable) {
		getPersonImmovables().add(personImmovable);
		personImmovable.setImmovableType(this);

		return personImmovable;
	}

	public PersonImmovable removePersonImmovable(PersonImmovable personImmovable) {
		getPersonImmovables().remove(personImmovable);
		personImmovable.setImmovableType(null);

		return personImmovable;
	}

}