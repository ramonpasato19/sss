package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * The persistent class for the transaction_batch_detail database table.
 * 
 */
@Entity
@Table(name = "transaction_batch_detail")
@View(members="transactionBatchDetailId;transactionBatchStatus;line;detail;errorMessage")
public class TransactionBatchDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="transaction_batch_detail_id", length=32)
	@ReadOnly
	@Hidden
	private String transactionBatchDetailId;

	@ManyToOne
	@JoinColumn(name = "transaction_batch_status_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", order = "name")
	@NoCreate
	@NoModify
	// @ReadOnly(notForViews="DEFAULT, NewTransactionBatch")
	private TransactionBatchStatus transactionBatchStatus;

	@ManyToOne
	@JoinColumn(name = "transaction_batch_id", nullable = false)
	@NoCreate
	@NoModify
	private TransactionBatch transactionBatch;

	@Column(name="detail", length = 200)
	private String detail;

	@Column(name = "line")
	private Integer line;
	
	@Column(name = "error_message", length = 4000)
	private String errorMessage;

	public TransactionBatchDetail() {

	}

	public String getTransactionBatchDetailId() {
		return transactionBatchDetailId;
	}

	public void setTransactionBatchDetailId(String transactionBatchDetailId) {
		this.transactionBatchDetailId = transactionBatchDetailId;
	}

	public TransactionBatchStatus getTransactionBatchStatus() {
		return transactionBatchStatus;
	}

	public void setTransactionBatchStatus(TransactionBatchStatus transactionBatchStatus) {
		this.transactionBatchStatus = transactionBatchStatus;
	}

	public TransactionBatch getTransactionBatch() {
		return transactionBatch;
	}

	public void setTransactionBatch(TransactionBatch transactionBatch) {
		this.transactionBatch = transactionBatch;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
