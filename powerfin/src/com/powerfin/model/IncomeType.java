package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the income_type database table.
 * 
 */
@Entity
@Table(name="income_type")
@View(members = "incomeTypeId;"
		+ "incomeClass; "
		+ "name; ")
public class IncomeType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="income_type_id", unique=true, nullable=false, length=3)
	private String incomeTypeId;

	@Column(name="income_class", nullable=false, length=3)
	private String incomeClass;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonIncome
	@OneToMany(mappedBy="incomeType")
	private List<PersonIncome> personIncomes;

	public IncomeType() {
	}

	public String getIncomeTypeId() {
		return this.incomeTypeId;
	}

	public void setIncomeTypeId(String incomeTypeId) {
		this.incomeTypeId = incomeTypeId;
	}

	public String getIncomeClass() {
		return this.incomeClass;
	}

	public void setIncomeClass(String incomeClass) {
		this.incomeClass = incomeClass;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonIncome> getPersonIncomes() {
		return this.personIncomes;
	}

	public void setPersonIncomes(List<PersonIncome> personIncomes) {
		this.personIncomes = personIncomes;
	}

	public PersonIncome addPersonIncome(PersonIncome personIncome) {
		getPersonIncomes().add(personIncome);
		personIncome.setIncomeType(this);

		return personIncome;
	}

	public PersonIncome removePersonIncome(PersonIncome personIncome) {
		getPersonIncomes().remove(personIncome);
		personIncome.setIncomeType(null);

		return personIncome;
	}

}