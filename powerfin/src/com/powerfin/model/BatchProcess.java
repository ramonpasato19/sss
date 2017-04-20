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
	@View(name="Execute", members="batchProcessId;"
			+ "accountingDate; "
			+ "batchProcessType;"
			+ "batchProcessStatus;"
			+ "countRequestDetails, countSatisfactoryDetails, countErrorDetails, countTotalDetails;"
			+ "batchProcessDetails;")
})
@Tabs({
	@Tab(properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name"),
	@Tab(name="Execute", properties="batchProcessId, accountingDate, batchProcessType.name, batchProcessStatus.name, batchProcessType.startEndDay", baseCondition = "${accountingDate} = (select o.accountingDate from Company o where companyId = 1)")
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
	@ReadOnly(forViews="Execute")
	private BatchProcessStatus batchProcessStatus;

	@ManyToOne
	@JoinColumn(name="batch_process_type_id", nullable=false)
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="Execute")
	private BatchProcessType batchProcessType;
	
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
	
	public Integer getCountErrorDetails() throws Exception
	{
		Integer count = 0;
		for (BatchProcessDetail detail : getBatchProcessDetails())
			if (detail.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_DETAIL_PROCESS_ERROR))
				count++;
		return count;
	}
	
	public Integer getCountSatisfactoryDetails() throws Exception
	{
		Integer count = 0;
		for (BatchProcessDetail detail : getBatchProcessDetails())
			if (detail.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_DETAIL_PROCESS_OK))
				count++;
		return count;
	}
	
	public Integer getCountRequestDetails() throws Exception
	{
		Integer count = 0;
		for (BatchProcessDetail detail : getBatchProcessDetails())
			if (detail.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_DETAIL_CREATE_STATUS))
				count++;
		return count;
	}
	
	public Integer getCountTotalDetails() throws Exception
	{
		return getBatchProcessDetails().size();
	}
	
	@PreCreate
	public void onCreate()
	{
		accountingDate = CompanyHelper.getCurrentAccountingDate();
	}
	
}