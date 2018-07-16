package com.powerfin.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.openxava.annotations.CollectionView;
import org.openxava.annotations.Condition;
import org.openxava.annotations.Editor;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListAction;
import org.openxava.annotations.ListActions;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Views({ @View(name = "InvoiceActiveReport", members = "fromDate;" + "toDate;" + "typeInvoice;"
		+ "Invoices{purchaseInvoices};" + "typeInvoiceSelected; ") })
public class ViewAccountInvoiceReportActive {

	@Column
	@Temporal(TemporalType.DATE)
	private Date fromDate;

	@Column
	@Temporal(TemporalType.DATE)
	private Date toDate;

	@Column(updatable = false)
	@Editor(value = "ValidValuesRadioButton")
	private TypeInvoice typeInvoice;

	public enum TypeInvoice {
		Purchase, Sale, ElectronicSale, Dispatch, ControlIncome, ControlExpense
	}

	@Transient
	@Hidden
	@ReadOnly
	private String typeInvoiceSelected;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.product.name, account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({ @ListAction("PurchaseInvoiceReportController.generatePdf"),
			@ListAction("PurchaseInvoiceReportController.generateExcel") })
	@Condition(value = "${account.product.productId} =  ${this.typeInvoiceSelected} "
			+ "and ${account.accountStatus.accountStatusId} in('002','005') "
			+ "and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("InvoiceActiveReport")
	private List<AccountInvoice> purchaseInvoices;

	public ViewAccountInvoiceReportActive() {

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

	public List<AccountInvoice> getPurchaseInvoices() {
		return purchaseInvoices;
	}

	public void setPurchaseInvoices(List<AccountInvoice> purchaseInvoices) {
		this.purchaseInvoices = purchaseInvoices;
	}

	public TypeInvoice getTypeInvoice() {
		return typeInvoice;
	}

	public void setTypeInvoice(TypeInvoice typeInvoice) {
		this.typeInvoice = typeInvoice;
	}

	public String getTypeInvoiceSelected() {
		return typeInvoiceSelected;
	}

	public void setTypeInvoiceSelected(String typeInvoiceSelected) {
		this.typeInvoiceSelected = typeInvoiceSelected;
	}
}