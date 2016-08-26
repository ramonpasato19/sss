package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_immovable database table.
 * 
 */
@Entity
@Table(name="person_immovable")
@NamedQuery(name="PersonImmovable.findAll", query="SELECT p FROM PersonImmovable p")
public class PersonImmovable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_immovable_id", unique=true, nullable=false, length=32)
	private String personImmovableId;

	@Column(length=400)
	private String address;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(nullable=false)
	private Integer mortgaged;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to ImmovableType
	@ManyToOne
	@JoinColumn(name="immovable_type_id", nullable=false)
	private ImmovableType immovableType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
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

	public Integer getMortgaged() {
		return this.mortgaged;
	}

	public void setMortgaged(Integer mortgaged) {
		this.mortgaged = mortgaged;
	}

	public Timestamp getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserRegistering() {
		return this.userRegistering;
	}

	public void setUserRegistering(String userRegistering) {
		this.userRegistering = userRegistering;
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