package com.powerfin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;

@Entity
@Table(name="account_item_tax")
@View(name="addTaxItem", members="tax; expireDate")
public class AccountItemTax {

	@Id
	@Column(name = "account_item_tax_id", unique = true)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemTaxId;

	@ManyToOne
	@JoinColumn(name="account_id")
	private AccountItem accountItem;

	@ManyToOne
	@JoinColumn(name="tax_id")
	@ReferenceView("Simple")
	private Tax tax;


	@Required
	@Column(name="expire_date")
	private Date expireDate;


	public String getAccountItemTaxId() {
		return accountItemTaxId;
	}
	public void setAccountItemTaxId(String accountItemTaxId) {
		this.accountItemTaxId = accountItemTaxId;
	}

	public Tax getTax() {
		return tax;
	}
	public void setTax(Tax tax) {
		this.tax = tax;
	}
	public AccountItem getAccountItem() {
		return accountItem;
	}
	public void setAccountItem(AccountItem accountItem) {
		this.accountItem = accountItem;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

}
