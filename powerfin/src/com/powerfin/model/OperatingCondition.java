package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="operating_condition")
@Views({
	@View(members="operatingConditionId; "
			+ "name;")
})
public class OperatingCondition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="operating_condition_id", unique=true, nullable=false, length=3)
	private String operatingConditionId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public OperatingCondition() {
	}

	public String getOperatingConditionId() {
		return operatingConditionId;
	}

	public void setOperatingConditionId(String operatingConditionId) {
		this.operatingConditionId = operatingConditionId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}