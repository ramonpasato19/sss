package com.powerfin.model;

import java.io.*;
import java.math.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the person_credit_card database table.
 * 
 */
@Entity
@Table(name="person_credit_card")
@View(members = "person;"
		+ "creditCardType;"
		+ "financialInstitution;"
		+ "cardNumber;"
		+ "balance;"
		+ "feeAmount")
public class PersonCreditCard extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_credit_card_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personCreditCardId;

	@Column(precision=11, scale=2)
	private BigDecimal balance;

	@Column(name="card_number", length=50)
	private String cardNumber;

	@Column(name="fee_amount", precision=11, scale=2)
	private BigDecimal feeAmount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	//bi-directional many-to-one association to CreditCardType
	@ManyToOne
	@JoinColumn(name="credit_card_type_id", nullable=false)
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	private CreditCardType creditCardType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
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