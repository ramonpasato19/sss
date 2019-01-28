package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="price_list")

public class PriceList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="price_list_id", unique=true, nullable=false, length=1)
	@Required
	private String priceListId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public PriceList() {
	}

	public String getPriceListId() {
		return priceListId;
	}

	public void setPriceListId(String priceListId) {
		this.priceListId = priceListId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}