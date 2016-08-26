package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_expense database table.
 * 
 */
@Entity
@Table(name="person_expense")
@NamedQuery(name="PersonExpense.findAll", query="SELECT p FROM PersonExpense p")
public class PersonExpense implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_expense_id", unique=true, nullable=false, length=32)
	private String personExpenseId;

	@Column(nullable=false, precision=11, scale=2)
	private BigDecimal amount;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to ExpenseType
	@ManyToOne
	@JoinColumn(name="expense_type_id", nullable=false)
	private ExpenseType expenseType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
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

}