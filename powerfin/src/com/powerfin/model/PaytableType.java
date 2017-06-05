package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the paytable_type database table.
 * 
 */
@Entity
@Table(name="paytable_type")
@Views({
	@View(members="paytableTypeId; "
			+ "name;")
})
public class PaytableType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="paytable_type_id", unique=true, nullable=false, length=3)
	private String paytableTypeId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public PaytableType() {
	}

	public String getPaytableTypeId() {
		return paytableTypeId;
	}

	public void setPaytableTypeId(String paytableTypeId) {
		this.paytableTypeId = paytableTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}