package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.types.Types;


/**
 * The persistent class for the transaction_module database table.
 * 
 */
@Entity
@Table(name="transaction_module")
@Views({
	@View(members="transactionModuleId;"
			+ "name;"
			+ "prefix, lpad, sequenceDBName, rpad, sufix;"
			+ "financialTransactionStatus;"
			+ "defaultTransactionStatus;"
			+ "allowsBatchProcess;"
			+ "allowsReverseTransaction"),
	@View(name="simple",
	members="transactionModuleId;"
			+ "name"),
	@View(name="forTransaction",
	members="name")
})
public class TransactionModule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="transaction_module_id", unique=true, nullable=false, length=30)
	private String transactionModuleId;

	@Column(nullable=false, length=100)
	@DisplaySize(50)
	private String name;

	@Column(length=10)
	private String prefix;

	@Column(length=10)
	private String sufix;
	
	@Column(length=10)
	private String lpad;
	
	@Column(length=10)
	private String rpad;
	
	@Column(name="sequence_db_name", length=50)
	private String sequenceDBName;

	@Column(name="allows_batch_process")
	@Required
	private Types.YesNoIntegerType allowsBatchProcess;
	
	@Column(name="allows_reverse_transaction")
	@Required
	private Types.YesNoIntegerType allowsReverseTransaction;
	
	//bi-directional many-to-one association to TransactionStatus
	@ManyToOne
	@JoinColumn(name="financial_transaction_status_id", nullable=false)
	@DescriptionsList(descriptionProperties="name")
	@NoCreate
	@NoModify
	private TransactionStatus financialTransactionStatus;

	//bi-directional many-to-one association to TransactionStatus
	@ManyToOne
	@JoinColumn(name="default_transaction_status_id", nullable=false)
	@DescriptionsList(descriptionProperties="name")
	@NoCreate
	@NoModify
	private TransactionStatus defaultTransactionStatus;
	
	public TransactionModule() {
	}

	public String getTransactionModuleId() {
		return this.transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSufix() {
		return sufix;
	}

	public void setSufix(String sufix) {
		this.sufix = sufix;
	}

	public String getLpad() {
		return lpad;
	}

	public void setLpad(String lpad) {
		this.lpad = lpad;
	}

	public String getRpad() {
		return rpad;
	}

	public void setRpad(String rpad) {
		this.rpad = rpad;
	}

	public String getSequenceDBName() {
		return sequenceDBName;
	}

	public void setSequenceDBName(String sequenceDBName) {
		this.sequenceDBName = sequenceDBName;
	}

	public TransactionStatus getFinancialTransactionStatus() {
		return financialTransactionStatus;
	}

	public void setFinancialTransactionStatus(
			TransactionStatus financialTransactionStatus) {
		this.financialTransactionStatus = financialTransactionStatus;
	}

	public TransactionStatus getDefaultTransactionStatus() {
		return defaultTransactionStatus;
	}

	public void setDefaultTransactionStatus(
			TransactionStatus defaultTransactionStatus) {
		this.defaultTransactionStatus = defaultTransactionStatus;
	}

	public Types.YesNoIntegerType getAllowsBatchProcess() {
		return allowsBatchProcess;
	}

	public void setAllowsBatchProcess(Types.YesNoIntegerType allowsBatchProcess) {
		this.allowsBatchProcess = allowsBatchProcess;
	}

	public Types.YesNoIntegerType getAllowsReverseTransaction() {
		return allowsReverseTransaction;
	}

	public void setAllowsReverseTransaction(Types.YesNoIntegerType allowsReverseTransaction) {
		this.allowsReverseTransaction = allowsReverseTransaction;
	}
	
}