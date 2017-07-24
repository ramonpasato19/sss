package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;

import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the movement database table.
 * 
 */
@Entity
@Table(name="movement")
@Views({
	@View(members="movementId; bookAccount; debitOrCredit; value;officialValue"),
	@View(name="AccountPayableMovement", members="voucher;"
			+ "transactionName;"
			+ "accountingDate; debitOrCredit; value;officialValue;"),
	@View(name="AccountingLedger", members="voucher;"
			+ "transactionName;"
			+ "accountingDate;bookAccount; debitOrCredit; value;officialValue;")
})
public class Movement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="movement_id", unique=true, nullable=false, length=32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String movementId;

	@Type(type="org.openxava.types.EnumStringType",
			   parameters={
				@Parameter(name="strings", value="D,C"), // These are the values stored on the database
				@Parameter(name="enumType", value="com.powerfin.model.types.Types$DebitOrCredit")
			   }
		 )
	@Column(name="debit_or_credit", nullable=false, length=1)
	@DisplaySize(20)
	private DebitOrCredit debitOrCredit;

	@Column(name="exchange_rate", nullable=false, precision=10, scale=7)
	private BigDecimal exchangeRate;
	
	@Column(name="official_value", nullable=false, precision=19, scale=2)
	private BigDecimal officialValue;

	@Column(nullable=false)
	private Integer subaccount;

	@Column(name="line")
	private Integer line;
	
	@Column(nullable=false, precision=19, scale=2)
	private BigDecimal value;

	@Column(length = 4000)
	private String remark;
	
	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private Account account;

	//bi-directional many-to-one association to BookAccount
	@ManyToOne
	@JoinColumn(name="book_account_id", nullable=false)
	@ReferenceView("Reference")
	private BookAccount bookAccount;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	private Category category;

	@ManyToOne
	@JoinColumn(name="financial_id", nullable=false)
	private Financial financial;

	@Column(name="quantity", nullable=false, precision=13, scale=4)
	private BigDecimal quantity;

	@ManyToOne
	@JoinColumn(name="unity_id")
	private Unity unity;

	@ManyToOne
	@JoinColumn(name="branch_id")
	@DescriptionsList(descriptionProperties = "name")
	private Branch branch;
	
	public Movement() {
	}

	public String getMovementId() {
		return this.movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public DebitOrCredit getDebitOrCredit() {
		return debitOrCredit;
	}

	public void setDebitOrCredit(DebitOrCredit debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}

	public BigDecimal getOfficialValue() {
		return this.officialValue==null?BigDecimal.ZERO:this.officialValue;
	}

	public void setOfficialValue(BigDecimal officialValue) {
		this.officialValue = officialValue;
	}

	public Integer getSubaccount() {
		return this.subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getValue() {
		return this.value==null?BigDecimal.ZERO:this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BookAccount getBookAccount() {
		return this.bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Financial getFinancial() {
		return financial;
	}

	public void setFinancial(Financial financial) {
		this.financial = financial;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getVoucher()
	{
		if (financial!=null && financial.getTransaction()!=null)
			return financial.getVoucher();
		return null;
	}
	
	public String getTransactionName()
	{
		if (financial!=null && financial.getTransaction()!=null)
			return financial.getTransaction().getTransactionModule().getName();
		return null;
	}
	
	public Date getAccountingDate()
	{
		if (financial!=null && financial.getTransaction()!=null)
			return financial.getAccountingDate();
		return null;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}
	
	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Movement");
		sb.append("|");
		sb.append(getBranch().getBranchId());
		sb.append("|");
		sb.append(getAccount().getAccountId());
		sb.append("|");
		sb.append(getSubaccount());
		sb.append("|");
		sb.append(getCategory().getCategoryId());
		sb.append("|");
		sb.append(getBookAccount().getGroupAccount().getGroupAccountId());
		sb.append("|");
		sb.append(getBookAccount().getBookAccountId());
		sb.append("|");
		sb.append(getDebitOrCredit());
		sb.append("|");
		sb.append(getValue());
		sb.append("|");
		sb.append(getOfficialValue());
		sb.append("|");
		sb.append(getExchangeRate());
		sb.append("|");
		sb.append(getMovementId());
		sb.append("|");
		sb.append(getUnity()!=null?getUnity().getUnityId():"");
		sb.append("|");
		sb.append(getQuantity()!=null?getQuantity():"");
		return sb.toString();
	}

}