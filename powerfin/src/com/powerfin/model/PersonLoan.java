package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the person_loan database table.
 * 
 */
@Entity
@Table(name="person_loan")
@NamedQuery(name="PersonLoan.findAll", query="SELECT p FROM PersonLoan p")
public class PersonLoan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_loan_id", unique=true, nullable=false, length=32)
	private String personLoanId;

	@Column(name="fee_amount", precision=11, scale=2)
	private BigDecimal feeAmount;

	@Column(name="financial_institution", length=100)
	private String financialInstitution;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to LoanType
	@ManyToOne
	@JoinColumn(name="loan_type_id", nullable=false)
	private LoanType loanType;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
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

}