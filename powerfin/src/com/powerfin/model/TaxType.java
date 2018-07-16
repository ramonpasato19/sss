package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name = "tax_type")
@Views({ @View(members = "taxTypeId; " + "name;") })
public class TaxType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "tax_type_id", unique = true, nullable = false, length = 6)
	private String taxTypeId;

	@Column(nullable = false, length = 100)
	@Required
	@DisplaySize(50)
	private String name;

	@Column(name="external_code", nullable = true, length = 50)
	@DisplaySize(30)
	private String externalCode;
	
	public TaxType() {
	}

	public String getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(String taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	
}