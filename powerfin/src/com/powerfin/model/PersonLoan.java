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
 * The persistent class for the person_loan database table.
 * 
 */
@Entity
@Table(name="person_loan")
@View(members = "person;"
		+ "loanType;"
		+ "financialInstitution;"
		+ "feeAmount;"
		+ "balance;"
		+ "scoreQualification")
public class PersonLoan extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_loan_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personLoanId;

	@Column(name="fee_amount", precision=11, scale=2)
	private BigDecimal feeAmount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	@Column(name="balance", precision=11, scale=2)
	private BigDecimal balance;
		
	@Column(name="score_qualification")
	private Integer scoreQualification;
	
	//bi-directional many-to-one association to LoanType
	@ManyToOne
	@JoinColumn(name="loan_type_id", nullable=false)
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	private LoanType loanType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
	private Person person;

	public PersonLoan() {
	}

	public String getPersonLoanId() {
		return this.personLoanId;
	}

	public void setPersonLoanId(String personLoanId) {
		this.personLoanId = personLoanId;
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

	public LoanType getLoanType() {
		return this.loanType;
	}

	public void setLoanType(LoanType loanType) {
		this.loanType = loanType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Integer getScoreQualification() {
		return scoreQualification;
	}

	public void setScoreQualification(Integer scoreQualification) {
		this.scoreQualification = scoreQualification;
	}

}