package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the unit_measure database table.
 * 
 */
@Entity
@Table(name="unit_measure")
@NamedQuery(name="UnitMeasure.findAll", query="SELECT u FROM UnitMeasure u")
public class UnitMeasure implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="unit_measure", unique=true, nullable=false, length=10)
	private String unitMeasure;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to AccountItem
	@OneToMany(mappedBy="unitMeasureBean")
	private List<AccountItem> accountItems;

	public UnitMeasure() {
	}

	public String getUnitMeasure() {
		return this.unitMeasure;
	}

	public void setUnitMeasure(String unitMeasure) {
		this.unitMeasure = unitMeasure;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AccountItem> getAccountItems() {
		return this.accountItems;
	}

	public void setAccountItems(List<AccountItem> accountItems) {
		this.accountItems = accountItems;
	}

	public AccountItem addAccountItem(AccountItem accountItem) {
		getAccountItems().add(accountItem);
		accountItem.setUnitMeasureBean(this);

		return accountItem;
	}

	public AccountItem removeAccountItem(AccountItem accountItem) {
		getAccountItems().remove(accountItem);
		accountItem.setUnitMeasureBean(null);

		return accountItem;
	}

}