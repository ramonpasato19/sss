package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;


/**
 * The persistent class for the transaction_submodule database table.
 * 
 */
@Entity
@Table(name="transaction_submodule")
@View(members="transactionModule;"
		+ "systemModule")
public class TransactionSubmodule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="transaction_submodule_id", unique=true, nullable=false, length=32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String transactionSubmoduleId;

	@Column(name="system_module", length=50)
	@Required
	private String systemModule;

	//bi-directional many-to-one association to TransactionModule
	@ManyToOne
	@JoinColumn(name="transaction_module_id", nullable=false)
	@ReferenceView(value="simple")
	@NoCreate
	@NoModify
	private TransactionModule transactionModule;

	public TransactionSubmodule() {
	}

	public String getTransactionSubmoduleId() {
		return this.transactionSubmoduleId;
	}

	public void setTransactionSubmoduleId(String transactionSubmoduleId) {
		this.transactionSubmoduleId = transactionSubmoduleId;
	}

	public String getSystemModule() {
		return this.systemModule;
	}

	public void setSystemModule(String systemModule) {
		this.systemModule = systemModule;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	

}