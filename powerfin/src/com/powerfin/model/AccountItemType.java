package com.powerfin.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Entity
@Table(name="account_item_type")
@Views({
	@View(name="selectionType",members="accountItemTypeId, name")
})

public class AccountItemType {
	@Id
	@Required
	@Column(name="account_item_type_id", length=3)
	private String accountItemTypeId;

	@Required
	@Column(name="name", length=100, nullable=true)
	private String name;

	@ManyToOne
	@JoinColumn(name="parent_account_item_type_id")
	@ReferenceView("selectionType")
	private AccountItemType parent;

	public String getAccountItemTypeId() {
		return accountItemTypeId;
	}

	public void setAccountItemTypeId(String accountItemTypeId) {
		this.accountItemTypeId = accountItemTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
