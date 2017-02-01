package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;

@Views({
@View(name="ConsultPurchaseInvoice", members="fromDate;"
		+ "toDate;"
		+ "purchaseInvoices{purchaseInvoices}; "
		+ "purchaseRetentions{purchaseRetentions};"),
@View(name="ConsultSaleInvoice", members="fromDate;"
		+ "toDate;"
		+ "saleInvoices{saleInvoices}; "
		+ "saleRetentions{saleRetentions};"),
})
public class ViewConsultAccountInvoice {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
		
	@OneToMany
	@ReadOnly
	@ListProperties(value="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({
		@ListAction("ConsultPurchaseInvoiceController.generatePdf"),
		@ListAction("ConsultPurchaseInvoiceController.generateExcel")
	})
	//@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"' "+
			"and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("ConsultPurchaseInvoice")
	private List<AccountInvoice> purchaseInvoices;

	@OneToMany
	@ReadOnly
	@ListProperties(value="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, accountInvoice.account.code, issueDate")
	@ListActions({
		@ListAction("ConsultPurchaseInvoiceController.generatePdf"),
		@ListAction("ConsultPurchaseInvoiceController.generateExcel")
	})
	//@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${account.product.productId} ='"+AccountInvoiceHelper.RETENTION_PURCHASE_PRODUCTID+"' "+
			"and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	private List<AccountRetention> purchaseRetentions;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, issueDate, balance")
	@ListActions({
		@ListAction("ConsultPurchaseInvoiceController.generatePdf"),
		@ListAction("ConsultPurchaseInvoiceController.generateExcel")
	})
	//@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"+
			"and ${issueDate} between ${this.fromDate} and ${this.toDate} ")
	@CollectionView("InvoiceSale")
	private List<AccountInvoice> saleInvoices;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, accountInvoice.account.code, issueDate")
	@ListActions({
		@ListAction("ConsultPurchaseInvoiceController.generatePdf"),
		@ListAction("ConsultPurchaseInvoiceController.generateExcel")
	})
	//@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${account.product.productId} ='"+AccountInvoiceHelper.RETENTION_SALE_PRODUCTID+"' "+
			"and ${accountInvoice.issueDate} between ${this.fromDate} and ${this.toDate} ")
	private List<AccountRetention> saleRetentions;
	
	public ViewConsultAccountInvoice() {
		
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
		/*
		List<AccountRetention> list = XPersistence.getManager()
				.createQuery("SELECT ar FROM AccountRetention ar, AccountInvoice ai "
						+ "WHERE ar.account.product.productId = :productId "
						+ "AND ar.account"
						+ "AND ${issueDate} between ${this.fromDate} and ${this.toDate} ")*/
		return saleRetentions;
	}

	public void setSaleRetentions(List<AccountRetention> saleRetentions) {
		this.saleRetentions = saleRetentions;
	}
	
}
