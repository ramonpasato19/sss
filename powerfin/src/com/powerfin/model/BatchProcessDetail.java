package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;


/**
 * The persistent class for the batch_process_detail database table.
 * 
 */
@Entity
@Table(name="batch_process_detail")
@Views({
	@View(members="batchProcessDetailId;"
			+ "batchProcess;"
			+ "account;"
			+ "batchProcessStatus;"
			+ "errorMessage"),
	@View(name="Reference", members="account;"
			+ "batchProcessStatus;"
			+ "errorMessage"),
})
		
public class BatchProcessDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "batch_process_detail_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String batchProcessDetailId;
	
	@ManyToOne
	@JoinColumn(name="batch_process_id", nullable=false)
	@ReferenceView("Reference")
	@ReadOnly(forViews="Reference")
	private BatchProcess batchProcess;
	
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	@ReferenceView("simple")
	@ReadOnly(forViews="Reference")
	private Account account;

	@ManyToOne
	@JoinColumn(name="batch_process_status_id", nullable=false)
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="Reference")
	private BatchProcessStatus batchProcessStatus;
	
	@Column(name="error_message", length = 4000)
	@ReadOnly(forViews="Reference")
	private String errorMessage;

	public BatchProcessDetail() {
		
	}

	public String getBatchProcessDetailId() {
		return batchProcessDetailId;
	}

	public void setBatchProcessDetailId(String batchProcessDetailId) {
		this.batchProcessDetailId = batchProcessDetailId;
	}

	public BatchProcess getBatchProcess() {
		return batchProcess;
	}

	public void setBatchProcess(BatchProcess batchProcess) {
		this.batchProcess = batchProcess;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BatchProcessStatus getBatchProcessStatus() {
		return batchProcessStatus;
	}

	public void setBatchProcessStatus(BatchProcessStatus batchProcessStatus) {
		this.batchProcessStatus = batchProcessStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}