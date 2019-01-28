package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;



/**
 * The persistent class for the account_item_branch database table.
 * 
 */
@Entity
@Table(name="account_item_price")
@View(members="#accountItem;"
				+ "priceList;"
				+ "price;"
				)
@Tab(properties="accountItem.accountId, accountItem.code, accountItem.name, priceList.priceListId, priceList.name, price")
public class AccountItemPrice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_item_price_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemPriceId; 

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	@ReferenceView("simple")
	@NoCreate
	@NoModify
	@Required
	private AccountItem accountItem;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	private PriceList priceList;

	@Column(name="price", precision=13, scale=4)
	private BigDecimal price;

	public AccountItemPrice() {
	}

	public String getAccountItemPriceId() {
		return accountItemPriceId;
	}

	public void setAccountItemPriceId(String accountItemPriceId) {
		this.accountItemPriceId = accountItemPriceId;
	}

	public AccountItem getAccountItem() {
		return accountItem;
	}

	public void setAccountItem(AccountItem accountItem) {
		this.accountItem = accountItem;
	}

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}