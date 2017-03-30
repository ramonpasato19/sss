package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the loan_type database table.
 * 
 */
@Entity
@Table(name="loan_type")
@View(members = "loanTypeId;"
		+ "name;")
public class LoanType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="loan_type_id", unique=true, nullable=false, length=3)
	private String loanTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonLoan
	@OneToMany(mappedBy="loanType")
	private List<PersonLoan> personLoans;

	public LoanType() {
	}

	public String getLoanTypeId() {
		return this.loanTypeId;
	}

	public void setLoanTypeId(String loanTypeId) {
		this.loanTypeId = loanTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonLoan> getPersonLoans() {
		return this.personLoans;
	}

	public void setPersonLoans(List<PersonLoan> personLoans) {
		this.personLoans = personLoans;
	}

	public PersonLoan addPersonLoan(PersonLoan personLoan) {
		getPersonLoans().add(personLoan);
		personLoan.setLoanType(this);

		return personLoan;
	}

	public PersonLoan removePersonLoan(PersonLoan personLoan) {
		getPersonLoans().remove(personLoan);
		personLoan.setLoanType(null);

		return personLoan;
	}

}