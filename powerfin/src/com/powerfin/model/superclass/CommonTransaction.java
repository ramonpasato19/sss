package com.powerfin.model.superclass;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.*;

@MappedSuperclass
public abstract class CommonTransaction extends AuditEntity{

	private String moduleName;
	
	// bi-directional one-to-one association to Transaction
	@OneToOne
	@JoinColumn(name = "transaction_id", nullable = false, insertable = false, updatable = false)
	protected Transaction transaction;
		
	@Transient
	@ManyToOne
	private TransactionModule transactionModule;
	
	@Transient
	@DisplaySize(25)
	@ReadOnly
	private String voucher;
	
	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	public String getVoucher() {
		if (transaction!=null)
			return transaction.getVoucher();
		else
			return null;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	
}