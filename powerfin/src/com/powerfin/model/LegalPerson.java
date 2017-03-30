package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the legal_person database table.
 * 
 */
@Entity
@Table(name="legal_person")
@View(members="#"
		+ "personId,"
		+ "name;"
		+ "generalInformation{#"
			+ "identificationType;"
			+ "identification;"
			+ "businessName;"
			+ "email;"
			+ "activity;"
			+ "incorporationDate;}"
		+ "workAddress{#"
			+ "homeDistrict;"
			+ "homeMainStreet, homeNumber; "
			+ "homeSideStreet;homeSector;"
			+ "homePhoneNumber1;homePhoneNumber2;}"
		+ "PersonIncome{personIncomes}"
		+ "PersonExpense{personExpenses}"
		+ "PersonDeposit{personDeposits}"
		+ "PersonLoan{personLoans}"
		+ "PersonImmovable{personImmovables}"
		+ "PersonMovable{personMovables}"
		+ "")
@Tab(properties="personId, person.identification, person.name, person.email")
public class LegalPerson extends CommonPerson implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="business_name", nullable=false, length=150)
	@Required
	private String businessName;

	@Temporal(TemporalType.DATE)
	@Column(name="incorporation_date")
	private Date incorporationDate;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="home_district_id")
	@NoCreate
	@NoModify
	@ReferenceView("WorkDistrict")
	private District homeDistrict;
		
	public LegalPerson() {
	}

	public String getBusinessName() {
		return this.businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Date getIncorporationDate() {
		return this.incorporationDate;
	}

	public void setIncorporationDate(Date incorporationDate) {
		this.incorporationDate = incorporationDate;
	}

	public District getHomeDistrict() {
		return homeDistrict;
	}

	public void setHomeDistrict(District homeDistrict) {
		this.homeDistrict = homeDistrict;
	}
	
	
}