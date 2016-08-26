package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="bookAccount;"
		+ "fromDate;"
		+ "toDate;"
		+ "movements;")
public class AccountingLedger {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchBookAccount.SearchMovementBookAccount")
	private BookAccount bookAccount;
	
	@ReadOnly
	private BigDecimal initialBalance;
	
	@ReadOnly
	private BigDecimal finalBalance;
	
	@OneToMany
	@ReadOnly
	@ListProperties(value="financial.accountingDate, "
			+ "financial.remark, "
			+ "remark, "
			+ "value, "
			+ "officialValue, "
			+ "financial.voucher, "
			+ "financial.transaction.transactionModule.name")
	@ListActions({
		@ListAction("AccountingLedgerController.generatePdf"),
		@ListAction("AccountingLedgerController.generateExcel")
	})
	@OrderBy("financial.accountingDate, financial.registrationDate, movementId")
	@Condition(value="${bookAccount.bookAccountId} = ${this.bookAccount.bookAccountId} "
			+ "and ${financial.accountingDate} between ${this.fromDate} and ${this.toDate}")
	@CollectionView("AccountingLedger")
	private List<Movement> movements;

	public AccountingLedger() {
		
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

	public BookAccount getBookAccount() {
		return bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
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

	public List<Movement> getMovements() {
		return movements;
	}

	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}

	
}
