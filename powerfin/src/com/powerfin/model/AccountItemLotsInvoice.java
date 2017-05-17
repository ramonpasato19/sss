package com.powerfin.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Table(name="account_item_lots_invoice")
@View(name="addInvoiceLots",members="account; quantity" )
public class AccountItemLotsInvoice {
	 
	@Id
	@Column(name = "account_item_lots_invoice_id", unique = true)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemLotsInvoiceId;
	
	@ManyToOne
	@JoinColumn(name="account_item_lots_id")
	private AccountItemLots accountItemLots;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	@ReferenceView("normal")
	@Required
	@NoCreate
	@NoModify
	@SearchAction(value="SearchAccount.SearchInvoicePurchaseAndSale")
	private Account account;
	
	@Column
	@Required
	private BigDecimal quantity;

	public String getAccountItemLotsInvoiceId() {
		return accountItemLotsInvoiceId;
	}

	public void setAccountItemLotsInvoiceId(String accountItemLotsInvoiceId) {
		this.accountItemLotsInvoiceId = accountItemLotsInvoiceId;
	}

	public AccountItemLots getAccountItemLots() {
		return accountItemLots;
	}

	public void setAccountItemLots(AccountItemLots accountItemLots) {
		this.accountItemLots = accountItemLots;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
}
