package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;



/**
 * The persistent class for the account_item_branch database table.
 * 
 */
@Entity
@Table(name="account_item_branch")
@View(members="#accountItem;"
				+ "branch;"
				+ "minimumStock;"
				+ "maximumStock;"
				+ "averageCost;"
				)
@Tab(properties="accountItem.accountId, accountItem.code, accountItem.name, branch.branchId, branch.name, minimumStock, averageCost")
public class AccountItemBranch implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_item_branch_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemBranchId; 

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	@ReferenceView("basic")
	@NoCreate
	@NoModify
	@Required
	private AccountItem accountItem;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	private Branch branch;

	@Column(name="minimum_stock", precision=19, scale=2)
	private BigDecimal minimumStock;

	@Column(name="maximum_stock", precision=19, scale=2)
	private BigDecimal maximumStock;

	@Column(name="average_cost", precision=15, scale=6)
	private BigDecimal averageCost;

	public AccountItemBranch() {
	}

	public String getAccountItemBranchId() {
		return accountItemBranchId;
	}

	public void setAccountItemBranchId(String accountItemBranchId) {
		this.accountItemBranchId = accountItemBranchId;
	}

	public AccountItem getAccountItem() {
		return accountItem;
	}

	public void setAccountItem(AccountItem accountItem) {
		this.accountItem = accountItem;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public BigDecimal getMinimumStock() {
		return minimumStock;
	}

	public void setMinimumStock(BigDecimal minimumStock) {
		this.minimumStock = minimumStock;
	}

	public BigDecimal getMaximumStock() {
		return maximumStock;
	}

	public void setMaximumStock(BigDecimal maximumStock) {
		this.maximumStock = maximumStock;
	}

	public BigDecimal getAverageCost() {
		return averageCost;
	}

	public void setAverageCost(BigDecimal averageCost) {
		this.averageCost = averageCost;
	}

}