package com.powerfin.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.openxava.annotations.CollectionView;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.DescriptionsLists;
import org.openxava.annotations.ListAction;
import org.openxava.annotations.ListActions;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.jpa.XPersistence;

@Views({
	@View(name="ViewAccountMovement", members="account;"
		+ "category;"
		+ "fromDate;"
		+ "toDate;"),
	@View(name="ViewAccountPayableMovement", members="account;"
			+ "category;"
			+ "fromDate;"
			+ "toDate;"),
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
		@DescriptionsList(forViews="ViewAccountPayableMovement", descriptionProperties="name", condition="${printable} = 1"),
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
	/*
	@Condition(value="${account.accountId} = ${this.account.accountId} "
			+ "and ${financial.accountingDate} between ${this.fromDate} and ${this.toDate} "
			+ "and ${category.categoryId} = ${this.category.categoryId} ")
	*/
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

	@SuppressWarnings("unchecked")
	public List<Movement> getMovements() {
		
		if (account==null)
			return null;
		if (fromDate.compareTo(toDate) == 0)
		{
			return XPersistence.getManager().createQuery("SELECT m FROM Movement m "
					+ "WHERE account.accountId = :accountId "
					+ "and financial.accountingDate = :accountingDate "
					+ "and category.categoryId = :categoryId ")
					.setParameter("accountId", account.getAccountId())
					.setParameter("accountingDate", fromDate)
					.setParameter("categoryId", category.getCategoryId())
					.getResultList();
		}
		else
		{
			return XPersistence.getManager().createQuery("SELECT m FROM Movement m "
					+ "WHERE account.accountId = :accountId "
					+ "and financial.accountingDate between :fromDate and :toDate"
					+ "and category.categoryId = :categoryId ")
					.setParameter("accountId", account.getAccountId())
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("categoryId", category.getCategoryId())
					.getResultList();
		}
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
