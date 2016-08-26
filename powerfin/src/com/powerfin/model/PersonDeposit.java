package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_deposit database table.
 * 
 */
@Entity
@Table(name="person_deposit")
@NamedQuery(name="PersonDeposit.findAll", query="SELECT p FROM PersonDeposit p")
public class PersonDeposit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_deposit_id", unique=true, nullable=false, length=32)
	private String personDepositId;

	@Column(name="account_number", length=50)
	private String accountNumber;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to DepositType
	@ManyToOne
	@JoinColumn(name="deposit_type_id", nullable=false)
	private DepositType depositType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	private Person person;

	public PersonDeposit() {
	}

	public String getPersonDepositId() {
		return this.personDepositId;
	}

	public void setPersonDepositId(String personDepositId) {
		this.personDepositId = personDepositId;
	}

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFinancialInstitution() {
		return this.financialInstitution;
	}

	public void setFinancialInstitution(String financialInstitution) {
		this.financialInstitution = financialInstitution;
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

	public DepositType getDepositType() {
		return this.depositType;
	}

	public void setDepositType(DepositType depositType) {
		this.depositType = depositType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}