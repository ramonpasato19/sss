package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;

@Views({ @View(name = "ConsultInvoiceActive", members = "fromDate;" + "toDate;" + "typeInvoice;"
		+ "Invoices{purchaseInvoices};" + "typeInvoiceSelected; ") })
public class ViewConsultAccountInvoiceActive {

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
		Purchase, Sale
	}

	@Transient
	@Hidden
	@ReadOnly
	private String typeInvoiceSelected;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({ @ListAction("ConsultPurchaseInvoiceController.generatePdf"),
			@ListAction("ConsultPurchaseInvoiceController.generateExcel") })
	@Condition(value = "${account.product.productType.productTypeId} =  ${this.typeInvoiceSelected} "
			+ "and ${account.accountStatus.accountStatusId} in('002','005') "
			+ "and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("ConsultInvoiceActive")
	private List<AccountInvoice> purchaseInvoices;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, accountInvoice.account.code, issueDate")
	@ListActions({ @ListAction("ConsultPurchaseInvoiceController.generatePdf"),
			@ListAction("ConsultPurchaseInvoiceController.generateExcel") })
	@Condition(value = "${account.product.productType.productTypeId} ='"
			+ AccountInvoiceHelper.RETENTION_PURCHASE_PRODUCT_TYPE_ID + "' "
			+ "and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	private List<AccountRetention> purchaseRetentions;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({ @ListAction("ConsultPurchaseInvoiceController.generatePdf"),
			@ListAction("ConsultPurchaseInvoiceController.generateExcel") })
	@Condition(value = "${account.product.productType.productTypeId} ='"
			+ AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID + "'"
			+ "and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("InvoiceSale")
	private List<AccountInvoice> saleInvoices;

	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, accountInvoice.account.code, issueDate")
	@ListActions({ @ListAction("ConsultPurchaseInvoiceController.generatePdf"),
			@ListAction("ConsultPurchaseInvoiceController.generateExcel") })
	@Condition(value = "${account.product.productType.productTypeId} ='"
			+ AccountInvoiceHelper.RETENTION_SALE_PRODUCT_TYPE_ID + "' "
			+ "and ${accountInvoice.issueDate} between ${this.fromDate} and ${this.toDate} ")
	private List<AccountRetention> saleRetentions;

	public ViewConsultAccountInvoiceActive() {

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

	public List<AccountInvoice> getSaleInvoices() {
		return saleInvoices;
	}

	public void setSaleInvoices(List<AccountInvoice> saleInvoices) {
		this.saleInvoices = saleInvoices;
	}

	public List<AccountRetention> getPurchaseRetentions() {
		return purchaseRetentions;
	}

	public void setPurchaseRetentions(List<AccountRetention> purchaseRetentions) {
		this.purchaseRetentions = purchaseRetentions;
	}

	public List<AccountRetention> getSaleRetentions() {
		return saleRetentions;
	}

	public void setSaleRetentions(List<AccountRetention> saleRetentions) {
		this.saleRetentions = saleRetentions;
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