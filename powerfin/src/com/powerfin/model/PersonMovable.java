package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;
import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the person_movable database table.
 * 
 */
@Entity
@Table(name="person_movable")
@View(members = "person;"
		+ "movableType;"
		+ "mark;"
		+ "model;"
		+ "year;"
		+ "amount;"
		+ "pledge")
public class PersonMovable extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_movable_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personMovableId;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(length=50)
	private String mark;

	@Column(length=50)
	private String model;

	@Column(name="pledge")
	private YesNoIntegerType pledge;

	@Column(nullable=false)
	private Integer year;

	//bi-directional many-to-one association to MovableType
	@ManyToOne
	@JoinColumn(name="movable_type_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="name")
	private MovableType movableType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
	private Person person;

	public PersonMovable() {
	}

	public String getPersonMovableId() {
		return this.personMovableId;
	}

	public void setPersonMovableId(String personMovableId) {
		this.personMovableId = personMovableId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public YesNoIntegerType getPledge() {
		return this.pledge;
	}

	public void setPledge(YesNoIntegerType pledge) {
		this.pledge = pledge;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public MovableType getMovableType() {
		return this.movableType;
	}

	public void setMovableType(MovableType movableType) {
		this.movableType = movableType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}