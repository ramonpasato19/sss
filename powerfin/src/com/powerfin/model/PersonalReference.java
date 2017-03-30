package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the personal_reference database table.
 * 
 */
@Entity
@Table(name="personal_reference")
@View(members = "person;"
		+ "nearFamily;"
		+ "relationship;"
		+ "address;"
		+ "homePhone;"
		+ "workPhone;"
		+ "cellPhone")
public class PersonalReference extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="personal_reference_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String personalReferenceId;

	@Column(length=100)
	@DisplaySize(50)
	private String address;

	@Column(name="cell_phone", length=50)
	private String cellPhone;

	@Column(name="home_phone", length=50)
	private String homePhone;

	@Column(name="near_family", length=100)
	@DisplaySize(50)
	private String nearFamily;

	@Column(length=50)
	private String relationship;

	@Column(name="work_phone", length=50)
	private String workPhone;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReferenceView("Reference")
	@Required
	private Person person;

	public PersonalReference() {
	}

	public String getPersonalReferenceId() {
		return this.personalReferenceId;
	}

	public void setPersonalReferenceId(String personalReferenceId) {
		this.personalReferenceId = personalReferenceId;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCellPhone() {
		return this.cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getHomePhone() {
		return this.homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getNearFamily() {
		return this.nearFamily;
	}

	public void setNearFamily(String nearFamily) {
		this.nearFamily = nearFamily;
	}

	public String getRelationship() {
		return this.relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getWorkPhone() {
		return this.workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}