package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.openxava.annotations.Tab;
import org.openxava.annotations.Tabs;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;


@Entity
@Table(name="kardex_account_temp")	
@Views({
	@View(members="accountId;"
		+ "issueDate, accountItemId, incomes; "
		+ "expenses;"
		+ "unitCost;"
		+ "totalCost, balance; "
		+ "accumulateBalance;"
		+ "accumulateTotalCost; averageCost;"
		+ "clientProvider ")
	
})
@Tabs({
	@Tab(properties="accountId, issueDate, accountItemId, incomes, expenses, unitCost")
})
public class KardexAccountTemp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="account_id", unique=true, nullable=false, length=40)	
	private String accountId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name= "issue_date")
	private Date issueDate;
	
	@Column(length=4000)
	private String remark;
	
	@Column(name="account_item_id", length=20)	
	private String accountItemId;
	
	@Column
	private BigDecimal incomes;
	
	@Column
	private BigDecimal expenses;
	
	@Column(name="unit_cost")
	private BigDecimal unitCost;
	
	@Column(name="total_cost")
	private BigDecimal totalCost;
	
	@Column
	private BigDecimal balance;
	
	@Column(name="accumulate_balance")
	private BigDecimal accumulateBalance;
	
	@Column(name="accumulate_total_cost")
	private BigDecimal accumulateTotalCost;
	
	@Column(name="average_cost")
	private BigDecimal averageCost;
	
	@Column(name="client_provider", length= 300)
	private String clientProvider;
	
	@Column(name="branch_id")
	private Integer branchId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="registration_date", nullable=false)
	private Date registrationDate;
	
	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;
	
	@Column(name="user_movement", nullable=false, length=30)
	private String userMovement;	

	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAccountItemId() {
		return accountItemId;
	}
	public void setAccountItemId(String accountItemId) {
		this.accountItemId = accountItemId;
	}

	public BigDecimal getIncomes() {
		return incomes;
	}
	public void setIncomes(BigDecimal incomes) {
		this.incomes = incomes;
	}

	public BigDecimal getExpenses() {
		return expenses;
	}
	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getAccumulateBalance() {
		return accumulateBalance;
	}
	public void setAccumulateBalance(BigDecimal accumulateBalance) {
		this.accumulateBalance = accumulateBalance;
	}

	public BigDecimal getAccumulateTotalCost() {
		return accumulateTotalCost;
	}
	public void setAccumulateTotalCost(BigDecimal accumulateTotalCost) {
		this.accumulateTotalCost = accumulateTotalCost;
	}

	public BigDecimal getAverageCost() {
		return averageCost;
	}
	public void setAverageCost(BigDecimal averageCost) {
		this.averageCost = averageCost;
	}

	public String getClientProvider() {
		return clientProvider;
	}
	public void setClientProvider(String clientProvider) {
		this.clientProvider = clientProvider;
	}
	public Integer getBranchId() {
		return branchId;
	}
	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getUserRegistering() {
		return userRegistering;
	}
	public void setUserRegistering(String userRegistering) {
		this.userRegistering = userRegistering;
	}
	public String getUserMovement() {
		return userMovement;
	}
	public void setUserMovement(String userMovement) {
		this.userMovement = userMovement;
	}
	
	

}