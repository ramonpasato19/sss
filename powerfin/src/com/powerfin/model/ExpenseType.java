package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the expense_type database table.
 * 
 */
@Entity
@Table(name="expense_type")
@View(members = "expenseTypeId;"
		+ "expenseClass;"
		+ "name;")
public class ExpenseType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="expense_type_id", unique=true, nullable=false, length=3)
	private String expenseTypeId;

	@Column(name="expense_class", nullable=false, length=3)
	private String expenseClass;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonExpense
	@OneToMany(mappedBy="expenseType")
	private List<PersonExpense> personExpenses;

	public ExpenseType() {
	}

	public String getExpenseTypeId() {
		return this.expenseTypeId;
	}

	public void setExpenseTypeId(String expenseTypeId) {
		this.expenseTypeId = expenseTypeId;
	}

	public String getExpenseClass() {
		return this.expenseClass;
	}

	public void setExpenseClass(String expenseClass) {
		this.expenseClass = expenseClass;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonExpense> getPersonExpenses() {
		return this.personExpenses;
	}

	public void setPersonExpenses(List<PersonExpense> personExpenses) {
		this.personExpenses = personExpenses;
	}

	public PersonExpense addPersonExpens(PersonExpense personExpens) {
		getPersonExpenses().add(personExpens);
		personExpens.setExpenseType(this);

		return personExpens;
	}

	public PersonExpense removePersonExpens(PersonExpense personExpens) {
		getPersonExpenses().remove(personExpens);
		personExpens.setExpenseType(null);

		return personExpens;
	}

}