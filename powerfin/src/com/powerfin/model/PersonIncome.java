package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_income database table.
 * 
 */
@Entity
@Table(name="person_income")
@NamedQuery(name="PersonIncome.findAll", query="SELECT p FROM PersonIncome p")
public class PersonIncome implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_income_id", unique=true, nullable=false, length=32)
	private String personIncomeId;

	@Column(nullable=false, precision=11, scale=2)
	private BigDecimal amount;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to IncomeType
	@ManyToOne
	@JoinColumn(name="income_type_id", nullable=false)
	private IncomeType incomeType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	private Person person;

	public PersonIncome() {
	}

	public String getPersonIncomeId() {
		return this.personIncomeId;
	}

	public void setPersonIncomeId(String personIncomeId) {
		this.personIncomeId = personIncomeId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public IncomeType getIncomeType() {
		return this.incomeType;
	}

	public void setIncomeType(IncomeType incomeType) {
		this.incomeType = incomeType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}