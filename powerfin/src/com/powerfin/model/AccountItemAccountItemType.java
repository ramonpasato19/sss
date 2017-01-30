package com.powerfin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.View;

@Entity
@Table(name="account_item_account_item_type")
@View(name="addTypeItem", members="accountItemType")
public class AccountItemAccountItemType {

	@Id
	@Column(name = "account_item_account_item_type_id", unique = true)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemAccountItemTypeId;

	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private AccountItem accountItem;

	@ManyToOne
	@JoinColumn(name="account_item_type_id", nullable=false)
	private AccountItemType accountItemType;



	public String getAccountItemAccountItemTypeId() {
		return accountItemAccountItemTypeId;
	}

	public void setAccountItemAccountItemTypeId(String accountItemAccountItemTypeId) {
		this.accountItemAccountItemTypeId = accountItemAccountItemTypeId;
	}

	public AccountItem getAccountItem() {
		return accountItem;
	}

	public void setAccountItem(AccountItem accountItem) {
		this.accountItem = accountItem;
	}

	public AccountItemType getAccountItemType() {
		return accountItemType;
	}

	public void setAccountItemType(AccountItemType accountItemType) {
		this.accountItemType = accountItemType;
	}


}
