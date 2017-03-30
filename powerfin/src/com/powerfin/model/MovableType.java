package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the movable_type database table.
 * 
 */
@Entity
@Table(name="movable_type")
@View(members = "movableTypeId;"
		+ "name;")
public class MovableType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="movable_type_id", unique=true, nullable=false, length=3)
	private String movableTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonMovable
	@OneToMany(mappedBy="movableType")
	private List<PersonMovable> personMovables;

	public MovableType() {
	}

	public String getMovableTypeId() {
		return this.movableTypeId;
	}

	public void setMovableTypeId(String movableTypeId) {
		this.movableTypeId = movableTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonMovable> getPersonMovables() {
		return this.personMovables;
	}

	public void setPersonMovables(List<PersonMovable> personMovables) {
		this.personMovables = personMovables;
	}

	public PersonMovable addPersonMovable(PersonMovable personMovable) {
		getPersonMovables().add(personMovable);
		personMovable.setMovableType(this);

		return personMovable;
	}

	public PersonMovable removePersonMovable(PersonMovable personMovable) {
		getPersonMovables().remove(personMovable);
		personMovable.setMovableType(null);

		return personMovable;
	}

}