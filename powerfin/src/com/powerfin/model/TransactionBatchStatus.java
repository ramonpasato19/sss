package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * The persistent class for the transaction_batch_status database table.
 * 
 */
@Entity
@Table(name="transaction_batch_status")
@View(members="transactionBatchStatusId;"
		+ "name")
public class TransactionBatchStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="transaction_batch_status_id", unique=true, nullable=false, length=3)
	private String transactionBatchStatusId;

	@Column(nullable=false, length=50)
	@Required
	@DisplaySize(20)
	private String name;

	public TransactionBatchStatus() {
	}

	public String getTransactionBatchStatusId() {
		return transactionBatchStatusId;
	}

	public void setTransactionBatchStatusId(String transactionBatchStatusId) {
		this.transactionBatchStatusId = transactionBatchStatusId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
