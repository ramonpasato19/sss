package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the deposit_type database table.
 * 
 */
@Entity
@Table(name="deposit_type")
@View(members = "depositTypeId;"
		+ "name;")
public class DepositType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="deposit_type_id", unique=true, nullable=false, length=3)
	private String depositTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonDeposit
	@OneToMany(mappedBy="depositType")
	private List<PersonDeposit> personDeposits;

	public DepositType() {
	}

	public String getDepositTypeId() {
		return this.depositTypeId;
	}

	public void setDepositTypeId(String depositTypeId) {
		this.depositTypeId = depositTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonDeposit> getPersonDeposits() {
		return this.personDeposits;
	}

	public void setPersonDeposits(List<PersonDeposit> personDeposits) {
		this.personDeposits = personDeposits;
	}

	public PersonDeposit addPersonDeposit(PersonDeposit personDeposit) {
		getPersonDeposits().add(personDeposit);
		personDeposit.setDepositType(this);

		return personDeposit;
	}

	public PersonDeposit removePersonDeposit(PersonDeposit personDeposit) {
		getPersonDeposits().remove(personDeposit);
		personDeposit.setDepositType(null);

		return personDeposit;
	}

}