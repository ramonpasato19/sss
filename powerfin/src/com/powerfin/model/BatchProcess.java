package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;


/**
 * The persistent class for the batch_process database table.
 * 
 */
@Entity
@Table(name="batch_process")
@Views({
	@View(members="batchProcessId;"
			+ "accountingDate; "
			+ "batchProcessType;"
			+ "batchProcessStatus;"
			+ "batchProcessDetails;"),
	@View(name="Process", members="batchProcessId;"
			+ "accountingDate; "
			+ "batchProcessType;"
			+ "batchProcessStatus;"
			+ "batchProcessDetails;")
})
@Tabs({
	@Tab(properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name"),
	@Tab(name="LoanInterestBatch", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name", baseCondition = "${batchProcessType.batchProcessTypeId} = 'LOAN_INTEREST'"),
	@Tab(name="ReceivableSalePortfolioBatch", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name", baseCondition = "${batchProcessType.batchProcessTypeId} = 'RECEIVABLE_SALE_PORTFOLIO'"),
	@Tab(name="PayableSalePortfolioBatch", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name", baseCondition = "${batchProcessType.batchProcessTypeId} = 'PAYABLE_SALE_PORTFOLIO'"),
	@Tab(name="UtilitySalePortfolioBatch", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name", baseCondition = "${batchProcessType.batchProcessTypeId} = 'UTILITY_SALE_PORTFOLIO'"),
	@Tab(name="SpreadPurchasePortfolioBatch", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name", baseCondition = "${batchProcessType.batchProcessTypeId} = 'SPREAD_PURCHASE_PORTFOLIO'"),
})
public class BatchProcess implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "batch_process_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id")
	@SequenceGenerator(name = "sequence_id", sequenceName = "batch_process_sequence", allocationSize = 1)
	@ReadOnly
	private Integer batchProcessId;

	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", nullable=false)
	@ReadOnly
	private Date accountingDate;
	
	@ManyToOne
	@JoinColumn(name="batch_process_status_id", nullable=false)
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ProcessBatch")
	private BatchProcessStatus batchProcessStatus;

	@ManyToOne
	@JoinColumn(name="batch_process_type_id", nullable=false)
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ProcessBatch")
	private BatchProcessType batchProcessType;
	
	//bi-directional many-to-one association to BatchProcessDetail
	@OneToMany(mappedBy="batchProcess")
	@ListProperties("account.accountId, batchProcessStatus.name, errorMessage")
	@ReadOnly
	private List<BatchProcessDetail> batchProcessDetails;
	
	public BatchProcess() {
		
	}

	public Integer getBatchProcessId() {
		return batchProcessId;
	}

	public void setBatchProcessId(Integer batchProcessId) {
		this.batchProcessId = batchProcessId;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public BatchProcessStatus getBatchProcessStatus() {
		return batchProcessStatus;
	}

	public void setBatchProcessStatus(BatchProcessStatus batchProcessStatus) {
		this.batchProcessStatus = batchProcessStatus;
	}

	public BatchProcessType getBatchProcessType() {
		return batchProcessType;
	}

	public void setBatchProcessType(BatchProcessType batchProcessType) {
		this.batchProcessType = batchProcessType;
	}

	public List<BatchProcessDetail> getBatchProcessDetails() {
		return batchProcessDetails;
	}

	public void setBatchProcessDetails(List<BatchProcessDetail> batchProcessDetails) {
		this.batchProcessDetails = batchProcessDetails;
	}
	
	@PreCreate
	public void onCreate()
	{
		accountingDate = CompanyHelper.getCurrentAccountingDate();
	}
	
}