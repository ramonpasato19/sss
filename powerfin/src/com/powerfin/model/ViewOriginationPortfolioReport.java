package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="broker;"
		+ "fromDate;"
		+ "toDate;"
		+ "projectedAccountingDate")
public class ViewOriginationPortfolioReport {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@Column
	private Date projectedAccountingDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("ShortReference")
	@SearchAction("SearchPerson.SearchBrokerPurchasePortfolio")
	private Person broker;
		
	@ReadOnly
	private BigDecimal initialBalance;
	
	@ReadOnly
	private BigDecimal finalBalance;
	
	/*
	@OneToMany
	@ReadOnly
	@ListProperties(value="financial.accountingDate, "
			+ "financial.remark, "
			+ "remark, "
			+ "value, "
			+ "financial.voucher, "
			+ "financial.transaction.transactionModule.name")
	@ListActions({
		@ListAction("AccountPayableMovementController.generatePdf"),
		@ListAction("AccountPayableMovementController.generateExcel")
	})
	@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${account.accountId} = ${this.account.accountId} "
			+ "and ${financial.accountingDate} between ${this.fromDate} and ${this.toDate} "
			+ "and ${category.categoryId} = ${this.category.categoryId} ")
	@CollectionView("AccountPayableMovement")
	private List<Movement> movements;
*/
	public ViewOriginationPortfolioReport() {
		
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Person getBroker() {
		return broker;
	}

	public void setBroker(Person broker) {
		this.broker = broker;
	}

	public BigDecimal getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}

	public BigDecimal getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

}
