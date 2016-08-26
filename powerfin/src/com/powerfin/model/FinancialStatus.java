package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;

import org.openxava.annotations.*;

import java.util.List;


/**
 * The persistent class for the financial_status database table.
 * 
 */
@Entity
@Table(name="financial_status")
@View(members="financialStatusId;"
		+ "name")
public class FinancialStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="financial_status_id", unique=true, nullable=false, length=3)
	private String financialStatusId;

	@Column(length=100, nullable=false)
	@Required
	private String name;

	//bi-directional many-to-one association to Financial
	@OneToMany(mappedBy="financialStatus")
	private List<Financial> financials;

	public FinancialStatus() {
	}

	public String getFinancialStatusId() {
		return this.financialStatusId;
	}

	public void setFinancialStatusId(String financialStatusId) {
		this.financialStatusId = financialStatusId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Financial> getFinancials() {
		return this.financials;
	}

	public void setFinancials(List<Financial> financials) {
		this.financials = financials;
	}

	public Financial addFinancial(Financial financial) {
		getFinancials().add(financial);
		financial.setFinancialStatus(this);

		return financial;
	}

	public Financial removeFinancial(Financial financial) {
		getFinancials().remove(financial);
		financial.setFinancialStatus(null);

		return financial;
	}

}