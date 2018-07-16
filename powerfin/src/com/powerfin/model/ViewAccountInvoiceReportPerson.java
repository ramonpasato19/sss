package com.powerfin.model;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="person; fromDate, toDate;Invoices{invoices};Retentions{retentions};")
public class ViewAccountInvoiceReportPerson {
	
	@Transient
	@ManyToOne
	@NoModify
	@NoCreate
	@ReferenceView("Reference")
	@SearchAction("SearchPerson.SearchInvoices")
	Person person;
	
	@Column
	Date fromDate;
	
	@Column
	Date toDate;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({ @ListAction("PurchaseInvoiceReportController.generatePdf"),
			@ListAction("PurchaseInvoiceReportController.generateExcel") })
	@Condition(value = 
			  "${account.person.personId} = ${this.person.personId} and " +
			   "${account.accountStatus.accountStatusId} in('002','003','005') " +
			 "and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("ConsultInvoiceActive")
	private List<AccountInvoice> invoices;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, accountInvoice.account.code, issueDate")
	@ListActions({
		@ListAction("PurchaseInvoiceReportController.generatePdf"),
		@ListAction("PurchaseInvoiceReportController.generateExcel")
	})
	@Condition(value=
			"${account.person.personId} = ${this.person.personId} "+
			"and ${accountInvoice.issueDate} between ${this.fromDate} and ${this.toDate} ")
	private List<AccountRetention> retentions;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<AccountInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<AccountInvoice> invoices) {
		this.invoices = invoices;
	}

	public List<AccountRetention> getRetentions() {
		return retentions;
	}

	public void setRetentions(List<AccountRetention> retentions) {
		this.retentions = retentions;
	}
	
}