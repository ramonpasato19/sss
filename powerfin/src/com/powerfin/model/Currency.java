package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the currency database table.
 * 
 */
@Entity
@Table(name="currency")
@View(members="currencyId;name;sign")
public class Currency implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="currency_id", unique=true, nullable=false, length=3)
	private String currencyId;

	@Column(nullable=false, length=100)
	private String name;
	
	@Column(length=100)
	private String sign;

	public Currency() {
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}