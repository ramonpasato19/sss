package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;

/**
 * The persistent class for the transaction_batch database table.
 * 
 */
@Entity
@Table(name = "transaction_batch")
@Views({
	@View(
			members="transactionBatchId;"
					+ "accountingDate;"
					+ "transactionModule;"
					+ "file;"
					+ "transactionBatchStatus"),
	@View(name="NewTransactionBatch",
			members="transactionBatchId;"
					+ "accountingDate;"
					+ "transactionModule;"
					+ "file;"
					+ "transactionBatchStatus"),
	@View(name="ProcessTransactionBatch",
	members="transactionBatchId;"
			+ "accountingDate;"
			+ "transactionModule;"
			+ "file;"
			+ "transactionBatchStatus;"
			+ "transactionBatchDetails"),
	@View(name="ConsultTransactionBatch",
	members="transactionBatchId;"
			+ "accountingDate;"
			+ "transactionModule;"
			+ "file;"
			+ "transactionBatchStatus;"
			+ "transactionBatchDetails"),
})
@Tabs({
	@Tab(name="NewTransactionBatch",properties="transactionBatchId,accountingDate,transactionModule.name,transactionBatchStatus.name", baseCondition = "${transactionBatchStatus.transactionBatchStatusId} = '001'"),
	@Tab(name="ProcessTransactionBatch",properties="transactionBatchId,accountingDate,transactionModule.name,transactionBatchStatus.name", baseCondition = "${transactionBatchStatus.transactionBatchStatusId} in ('001','002','003')"),
	@Tab(name="ConsultTransactionBatch",properties="transactionBatchId,accountingDate,transactionModule.name,transactionBatchStatus.name", baseCondition = "${transactionBatchStatus.transactionBatchStatusId} in ('001','002','003','004')"),
})
public class TransactionBatch extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "transaction_batch_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id")
	@SequenceGenerator(name = "sequence_id", sequenceName = "transaction_batch_sequence", allocationSize = 1)
	@ReadOnly(notForViews = "Reference")
	private Integer transactionBatchId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accounting_date", length = 15, nullable = false)
	@ReadOnly
	private Date accountingDate;

	@ManyToOne
	@JoinColumn(name = "transaction_batch_status_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", order = "name")
	@NoCreate
	@NoModify
	@ReadOnly(notForViews="DEFAULT, NewTransactionBatch")
	private TransactionBatchStatus transactionBatchStatus;

	@ManyToOne
	@JoinColumn(name = "transaction_module_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", order = "name", condition="${allowsBatchProcess} = 1")
	@NoCreate
	@NoModify
	@Required
	@ReadOnly(notForViews="DEFAULT, NewTransactionBatch")
	private TransactionModule transactionModule;

	@Stereotype("FILE")
	@Column(length = 32)
	@Required
	@ReadOnly(notForViews="DEFAULT, NewTransactionBatch")
	private String file;

	//bi-directional many-to-one association to BatchProcessDetail
	@OneToMany(mappedBy="transactionBatch")
	@ListAction("NewTransactionBatchController.generateExcel")
	@ListProperties("line, detail,transactionBatchStatus.name, errorMessage")
	@ReadOnly
	private List<TransactionBatchDetail> transactionBatchDetails;
	
	public TransactionBatch() {

	}

	public Integer getTransactionBatchId() {
		return transactionBatchId;
	}

	public void setTransactionBatchId(Integer transactionBatchId) {
		this.transactionBatchId = transactionBatchId;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public TransactionBatchStatus getTransactionBatchStatus() {
		return transactionBatchStatus;
	}

	public void setTransactionBatchStatus(TransactionBatchStatus transactionBatchStatus) {
		this.transactionBatchStatus = transactionBatchStatus;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<TransactionBatchDetail> getTransactionBatchDetails() {
		return transactionBatchDetails;
	}

	public void setTransactionBatchDetails(List<TransactionBatchDetail> transactionBatchDetails) {
		this.transactionBatchDetails = transactionBatchDetails;
	}

	@PreCreate
	public void onCreate()
	{
		setAccountingDate(CompanyHelper.getCurrentAccountingDate());
		setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthRequestStatus());
	}
}
