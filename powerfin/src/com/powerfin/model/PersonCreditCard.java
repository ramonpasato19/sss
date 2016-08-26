package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_credit_card database table.
 * 
 */
@Entity
@Table(name="person_credit_card")
@NamedQuery(name="PersonCreditCard.findAll", query="SELECT p FROM PersonCreditCard p")
public class PersonCreditCard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_credit_card_id", unique=true, nullable=false, length=32)
	private String personCreditCardId;

	@Column(precision=11, scale=2)
	private BigDecimal balance;

	@Column(name="card_number", length=50)
	private String cardNumber;

	@Column(name="fee_amount", precision=11, scale=2)
	private BigDecimal feeAmount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to CreditCardType
	@ManyToOne
	@JoinColumn(name="credit_card_type_id", nullable=false)
	private CreditCardType creditCardType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	private Person person;

	public PersonCreditCard() {
	}

	public String getPersonCreditCardId() {
		return this.personCreditCardId;
	}

	public void setPersonCreditCardId(String personCreditCardId) {
		this.personCreditCardId = personCreditCardId;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public BigDecimal getFeeAmount() {
		return this.feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
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

	public CreditCardType getCreditCardType() {
		return this.creditCardType;
	}

	public void setCreditCardType(CreditCardType creditCardType) {
		this.creditCardType = creditCardType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}