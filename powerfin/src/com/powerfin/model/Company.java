package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.Currency;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name="company")
@Views({
	@View(members="companyId;name;oxorganizationId;accountingDate;person;officialCurrency"),
	@View(name="AccountingClosingDay", members="companyId;name;accountingDate;")
})
@Tabs({
	@Tab(properties="companyId, name, accountingDate, person.name, officialCurrency.currencyId"),
	@Tab(name="AccountingClosingDay", properties="companyId, name, accountingDate")
})
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="company_id", unique=true, nullable=false)
	@ReadOnly
	private Integer companyId;

	@Column(nullable=false, length=100)
	@ReadOnly(forViews="AccountingClosingDay")
	private String name;

	@Column(name="oxorganization_id", length=50)
	@ReadOnly(forViews="AccountingClosingDay")
	private String oxorganizationId;
	
	@Column(name="logo", length=50)
	private String logo;
	
	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", nullable=false)
	private Date accountingDate;
	
	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReadOnly(forViews="AccountingClosingDay")
	@NoCreate
	@NoModify
	private Person person;
	
	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="official_currency_id", nullable=false)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AccountingClosingDay")
	private Currency officialCurrency;

	public Company() {
	}

	public Integer getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ ACTUALIZA NOMBRE: "+this.name+">"+name);
		this.name = name;
	}

	public String getOxorganizationId() {
		return oxorganizationId;
	}

	public void setOxorganizationId(String oxorganizationId) {
		this.oxorganizationId = oxorganizationId;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ ACTUALIZA FECHA: "+this.accountingDate+">"+accountingDate);
		this.accountingDate = accountingDate;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Currency getOfficialCurrency() {
		return officialCurrency;
	}

	public void setOfficialCurrency(Currency officialCurrency) {
		this.officialCurrency = officialCurrency;
	}

}