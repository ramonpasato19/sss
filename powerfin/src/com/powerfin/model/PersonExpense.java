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
 * The persistent class for the person_expense database table.
 * 
 */
@Entity
@Table(name="person_expense")
@View(members = "person;"
		+ "expenseType;"
		+ "amount;"
		+ "description;")
public class PersonExpense extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_expense_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personExpenseId;

	@Column(nullable=false, precision=11, scale=2)
	private BigDecimal amount;

	//bi-directional many-to-one association to ExpenseType
	@ManyToOne
	@JoinColumn(name="expense_type_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="expenseClass, name")
	private ExpenseType expenseType;

	@Column(length=200)
	private String description;
	
	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
	private Person person;

	public PersonExpense() {
	}

	public String getPersonExpenseId() {
		return this.personExpenseId;
	}

	public void setPersonExpenseId(String personExpenseId) {
		this.personExpenseId = personExpenseId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public ExpenseType getExpenseType() {
		return this.expenseType;
	}

	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
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