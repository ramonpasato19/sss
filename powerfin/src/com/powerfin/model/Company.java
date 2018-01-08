package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.model.Currency;
import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name="company")
@Views({
	@View(members="companyId;name;oxorganizationId;accountingDate;person;officialCurrency"),
	@View(name="AccountingClosingDay", members="companyId; name; accountingDate; nextAccountingDate; batchProcesses"),
	@View(name="Backup", members="companyId; name; oxorganizationId; accountingDate; output"),
	@View(name="Log", members="companyId; accountingDate; name; oxorganizationId; lines; output")
})
@Tabs({
	@Tab(properties="companyId, name, accountingDate, person.name, officialCurrency.currencyId"),
	@Tab(name="AccountingClosingDay", properties="companyId, name, accountingDate"),
	@Tab(name="Backup", properties="companyId, name, accountingDate"),
	@Tab(name="Log", properties="companyId, name, accountingDate")
})
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="company_id", unique=true, nullable=false)
	@ReadOnly
	private Integer companyId;

	@Column(nullable=false, length=100)
	@ReadOnly(forViews="AccountingClosingDay, Backup, Log")
	private String name;

	@Column(name="oxorganization_id", length=50)
	@ReadOnly(forViews="AccountingClosingDay, Backup, Log")
	private String oxorganizationId;
	
	@Column(name="logo", length=50)
	private String logo;
	
	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", nullable=false)
	@ReadOnly(forViews="AccountingClosingDay, Backup, Log")
	private Date accountingDate;
	
	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@ReadOnly(forViews="AccountingClosingDay, Backup, Log")
	@NoCreate
	@NoModify
	private Person person;
	
	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="official_currency_id", nullable=false)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AccountingClosingDay, Backup, Log")
	private Currency officialCurrency;

	@Transient
	@ListProperties("name, startEndDay, activated, batchProcessStatus, countRequestDetails, countErrorDetails, countSatisfactoryDetails, countTotalDetails")
	@ReadOnly
	@CollectionView(value = "AccountingClosingDay")
	private List<BatchProcessType> batchProcesses;
		
	@Transient
	@Temporal(TemporalType.DATE)
	@ReadOnly
	private Date nextAccountingDate;
	
	@Transient
	@Stereotype("TEXT_AREA")
	@ReadOnly(notForViews="Log")
	private String output;
	
	@Transient
	private Integer lines;
	
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

	public Date getNextAccountingDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getAccountingDate());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public void setNextAccountingDate(Date nextAccountingDate) {
		this.nextAccountingDate = nextAccountingDate;
	}

	@SuppressWarnings("unchecked")
	public List<BatchProcessType> getBatchProcesses() {
		return XPersistence.getManager().createQuery("SELECT o FROM BatchProcessType o "
				+ "WHERE o.activated = :activated "
				+ "ORDER BY o.name ")
				.setParameter("activated", YesNoIntegerType.YES)
				.getResultList();
	}

	public void setBatchProcesses(List<BatchProcessType> batchProcesses) {
		this.batchProcesses = batchProcesses;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Integer getLines() {
		return lines;
	}

	public void setLines(Integer lines) {
		this.lines = lines;
	}

}