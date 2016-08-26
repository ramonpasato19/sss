package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_movable database table.
 * 
 */
@Entity
@Table(name="person_movable")
@NamedQuery(name="PersonMovable.findAll", query="SELECT p FROM PersonMovable p")
public class PersonMovable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_movable_id", unique=true, nullable=false, length=32)
	private String personMovableId;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(length=50)
	private String mark;

	@Column(length=50)
	private String model;

	@Column(precision=1)
	private BigDecimal pledge;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	@Column(nullable=false)
	private Integer year;

	//bi-directional many-to-one association to MovableType
	@ManyToOne
	@JoinColumn(name="movable_type_id", nullable=false)
	private MovableType movableType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
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

	public BigDecimal getPledge() {
		return this.pledge;
	}

	public void setPledge(BigDecimal pledge) {
		this.pledge = pledge;
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