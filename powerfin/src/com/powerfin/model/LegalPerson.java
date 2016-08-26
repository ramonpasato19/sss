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
		+ "personId;"
		+ "identificationType;"
		+ "identification;"
		+ "businessName;"
		+ "homePhoneNumber1;"
		+ "email;"
		+ "address[#homeMainStreet, homeNumber; "
		+ "homeSideStreet;homeSector]"
		+ "")
@Tab(properties="personId, person.identification, person.name")
public class LegalPerson extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private Integer personId;

	@Column(name="business_name", nullable=false, length=150)
	@Required
	private String businessName;

	@Temporal(TemporalType.DATE)
	@Column(name="incorporation_date")
	private Date incorporationDate;


	@Column(name="home_main_street", length=100)
	private String homeMainStreet;

	@Column(name="home_number", length=50)
	@DisplaySize(20)
	private String homeNumber;

	@Column(name="home_phone_number_1", length=50)
	private String homePhoneNumber1;
	

	@Column(name="home_sector", length=100)
	private String homeSector;

	@Column(name="home_side_street", length=100)
	private String homeSideStreet;
	
	//bi-directional one-to-one association to Person
	@OneToOne
	@JoinColumn(name="person_id", nullable=false, insertable=false, updatable=false)
	private Person person;


	@Transient
	@DisplaySize(20)
	@Required
	private String identification;
	
	@Transient
	@DisplaySize(20)
	private String email;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	private IdentificationType identificationType;
	
	public LegalPerson() {
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
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

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getHomeMainStreet() {
		return homeMainStreet;
	}

	public void setHomeMainStreet(String homeMainStreet) {
		this.homeMainStreet = homeMainStreet;
	}

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getHomePhoneNumber1() {
		return homePhoneNumber1;
	}

	public void setHomePhoneNumber1(String homePhoneNumber1) {
		this.homePhoneNumber1 = homePhoneNumber1;
	}

	public String getHomeSector() {
		return homeSector;
	}

	public void setHomeSector(String homeSector) {
		this.homeSector = homeSector;
	}

	public String getHomeSideStreet() {
		return homeSideStreet;
	}

	public void setHomeSideStreet(String homeSideStreet) {
		this.homeSideStreet = homeSideStreet;
	}

	public String getIdentification() {
		if(person!=null)
			return person.getIdentification();
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public IdentificationType getIdentificationType() {
		if(person!=null)
			return person.getIdentificationType();
		return identificationType;
	}

	public void setIdentificationType(IdentificationType identificationType) {
		this.identificationType = identificationType;
	}

	public String getEmail() {
		if(person!=null)
			return person.getEmail();
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}