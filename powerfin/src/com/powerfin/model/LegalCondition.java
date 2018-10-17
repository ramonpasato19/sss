package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="legal_condition")
@Views({
	@View(members="legalConditionId; "
			+ "name;")
})
public class LegalCondition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="legal_condition_id", unique=true, nullable=false, length=3)
	private String legalConditionId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public LegalCondition() {
	}

	public String getLegalConditionId() {
		return legalConditionId;
	}

	public void setLegalConditionId(String legalConditionId) {
		this.legalConditionId = legalConditionId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}