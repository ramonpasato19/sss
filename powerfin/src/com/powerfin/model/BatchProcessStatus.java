package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="batch_process_status")
@Views({
	@View(members="batchProcessStatusId; "
			+ "name;")
})
public class BatchProcessStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="batch_process_status_id", unique=true, nullable=false, length=3)
	private String batchProcessStatusId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;

	public BatchProcessStatus() {
	}

	public String getBatchProcessStatusId() {
		return batchProcessStatusId;
	}

	public void setBatchProcessStatusId(String batchProcessStatusId) {
		this.batchProcessStatusId = batchProcessStatusId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}