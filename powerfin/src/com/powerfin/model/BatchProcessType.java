package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="batch_process_type")
@Views({
	@View(members="batchProcessTypeId; "
			+ "name;")
})
public class BatchProcessType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="batch_process_type_id", unique=true, nullable=false, length=50)
	private String batchProcessTypeId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public BatchProcessType() {
	}

	public String getBatchProcessTypeId() {
		return batchProcessTypeId;
	}

	public void setBatchProcessTypeId(String batchProcessTypeId) {
		this.batchProcessTypeId = batchProcessTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}