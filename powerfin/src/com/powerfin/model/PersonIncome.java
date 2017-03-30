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
 * The persistent class for the person_income database table.
 * 
 */
@Entity
@Table(name="person_income")
@View(members = "person; "
		+ "incomeType; "
		+ "amount; "
		+ "description; ")
public class PersonIncome extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_income_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personIncomeId;

	@Column(nullable=false, precision=11, scale=2)
	private BigDecimal amount;

	@Column(length=200)
	private String description;
	
	@ManyToOne
	@JoinColumn(name="income_type_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="incomeClass, name")
	private IncomeType incomeType;

	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}