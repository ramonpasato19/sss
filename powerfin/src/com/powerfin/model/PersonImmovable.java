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
 * The persistent class for the person_immovable database table.
 * 
 */
@Entity
@Table(name="person_immovable")
@View(members = "person;"
		+ "immovableType;"
		+ "address;"
		+ "amount;"
		+ "mortgaged")
public class PersonImmovable extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_immovable_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personImmovableId;

	@Column(length=400)
	@Stereotype("MEMO")
	private String address;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(nullable=false)
	private YesNoIntegerType mortgaged;

	//bi-directional many-to-one association to ImmovableType
	@ManyToOne
	@JoinColumn(name="immovable_type_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="name")
	private ImmovableType immovableType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
	private Person person;

	public PersonImmovable() {
	}

	public String getPersonImmovableId() {
		return this.personImmovableId;
	}

	public void setPersonImmovableId(String personImmovableId) {
		this.personImmovableId = personImmovableId;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public YesNoIntegerType getMortgaged() {
		return this.mortgaged;
	}

	public void setMortgaged(YesNoIntegerType mortgaged) {
		this.mortgaged = mortgaged;
	}

	public ImmovableType getImmovableType() {
		return this.immovableType;
	}

	public void setImmovableType(ImmovableType immovableType) {
		this.immovableType = immovableType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}