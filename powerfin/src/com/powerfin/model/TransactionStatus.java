package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;

import org.openxava.annotations.*;

import java.util.List;


/**
 * The persistent class for the transaction_status database table.
 * 
 */
@Entity
@Table(name="transaction_status")
@Views({
	@View(members="transactionStatusId,name"),
	@View(name="simple", members="name"),
})
public class TransactionStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="transaction_status_id", unique=true, nullable=false, length=3)
	private String transactionStatusId;

	@Column(length=100, nullable=false)
	private String name;

	//bi-directional many-to-one association to Transaction
	@OneToMany(mappedBy="transactionStatus")
	private List<Transaction> transactions;

	public TransactionStatus() {
	}

	public String getTransactionStatusId() {
		return this.transactionStatusId;
	}

	public void setTransactionStatusId(String transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Transaction> getTransactions() {
		return this.transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Transaction addTransaction(Transaction transaction) {
		getTransactions().add(transaction);
		transaction.setTransactionStatus(this);

		return transaction;
	}

	public Transaction removeTransaction(Transaction transaction) {
		getTransactions().remove(transaction);
		transaction.setTransactionStatus(null);

		return transaction;
	}

}