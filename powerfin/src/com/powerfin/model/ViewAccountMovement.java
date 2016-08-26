package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@Views({
	@View(name="ViewAccountMovement", members="account;"
		+ "category;"
		+ "fromDate;"
		+ "toDate;"
		+ "movements;"),
	@View(name="ViewAccountPayableMovement", members="account;"
			+ "category;"
			+ "fromDate;"
			+ "toDate;"
			+ "movements;"),
})
public class ViewAccountMovement {

	@Column
	private Date fromDate;
	
	@Column
	private Date toDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("normal")
	@SearchAction(forViews="ViewAccountPayableMovement", value="SearchGeneralAccount.SearchPayableAccount")
	private Account account;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsLists({
		@DescriptionsList(forViews="ViewAccountPayableMovement", descriptionProperties="name", condition="${categoryId} in ('BALANCE','ADVANCE','ADVSALPORT')"),
		@DescriptionsList(forViews="ViewAccountMovement", descriptionProperties="categoryId, name")
	})
	private Category category;
	
	
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

	public ViewAccountMovement() {
		
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	
}
