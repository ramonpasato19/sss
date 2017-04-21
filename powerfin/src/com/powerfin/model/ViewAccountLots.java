package com.powerfin.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OrderBy;
import org.openxava.annotations.Condition;
import org.openxava.annotations.ListAction;
import org.openxava.annotations.ListActions;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.View;

@View(name="consultAccountLots", members="fromDate,toDate;AccountItem{accountItems};")
public class ViewAccountLots {
	
	@Column
	private Date fromDate;

	@Column
	private Date toDate;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value = "account.accountId, account.currency.currencyId, account.name, numberLot , code, expireDate, quantity, currentQuantity")
	@ListActions({ @ListAction("ConsultPurchaseInvoiceController.generatePdf"),
			@ListAction("ConsultPurchaseInvoiceController.generateExcel") })
	@Condition(value = "${expireDate} between ${this.fromDate} and ${this.toDate} and ${currentQuantity} > 0 and ${active}=true ")
	@OrderBy(clause="${expireDate} asc")
	private List<AccountItemLots> accountItems;
	
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

	public List<AccountItemLots> getAccountItems() {
		return accountItems;
	}

	public void setAccountItems(List<AccountItemLots> accountItems) {
		this.accountItems = accountItems;
	}
}