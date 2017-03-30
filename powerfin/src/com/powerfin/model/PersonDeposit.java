package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the person_deposit database table.
 * 
 */
@Entity
@Table(name="person_deposit")
@View(members = "person;"
		+ "depositType;"
		+ "financialInstitution;"
		+ "accountNumber;"
		+ "openingDate;"
		+ "amountLetter;"
		+ "amount")
public class PersonDeposit extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_deposit_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personDepositId;

	@Column(name="account_number", length=50)
	private String accountNumber;

	@Column(precision=11, scale=2)
	private BigDecimal amount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	@Column(name="amount_letter", length = 100)
	private String amountLetter ;
	
	@Column(name = "opening_date")
	@Temporal(TemporalType.DATE)
	private Date openingDate;
	
	//bi-directional many-to-one association to DepositType
	@ManyToOne
	@JoinColumn(name="deposit_type_id", nullable=false)
	@DescriptionsList(descriptionProperties="name", order="name")
	@NoCreate
	@NoModify
	@Required
	private DepositType depositType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
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

	public String getAmountLetter() {
		return amountLetter;
	}

	public void setAmountLetter(String amountLetter) {
		this.amountLetter = amountLetter;
	}

	public Date getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

}