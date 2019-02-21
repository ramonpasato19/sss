package com.powerfin.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.DescriptionsLists;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Views({
	@View(name="ViewAccountMovement", members="account;"
		+ "category;"
		+ "subaccount;"
		+ "branch;"
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
	@Required
	private Account account;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsLists({
		@DescriptionsList(forViews="ViewAccountPayableMovement", descriptionProperties="name", condition="${printable} = 1"),
		@DescriptionsList(forViews="ViewAccountMovement", descriptionProperties="categoryId, name")
	})
	@Required
	private Category category;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsList
	private Branch branch;
	
	@Column
	private Integer subaccount;
	
	@ReadOnly
	private BigDecimal initialBalance;
	
	@ReadOnly
	private BigDecimal finalBalance;

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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Integer getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

}
