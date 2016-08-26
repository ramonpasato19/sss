package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;


/**
 * The persistent class for the book_account database table.
 * 
 */
@Entity
@Table(name="book_account")
@Views({
	@View(members="groupAccount; "
			+ "currency; "
			+ "bookAccountId; "
			+ "externalCode; "
			+ "name; "
			+ "level; "
			+ "movement; "
			+ "allowManualEntry; "
			+ "allowCurrencyAdjustment; "
			+ "bookAccountParent;"),
	@View(name="Reference", members="bookAccountId; name;")
})
public class BookAccount extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="book_account_id", unique=true, nullable=false, length=50)
	@DisplaySize(20)
	private String bookAccountId;

	@Column(name="book_account_parent", nullable=true, length=50)
	private String bookAccountParent;

	@Column(nullable=false)
	@Required
	private Integer level;

	@Column(nullable=false)
	@Required
	private Types.YesNoIntegerType movement;

	@Column(name="allow_manual_entry", nullable=false)
	@Required
	private Types.YesNoIntegerType allowManualEntry;
	
	@Column(name="allow_currency_adjustment", nullable=false)
	@Required
	private Types.YesNoIntegerType allowCurrencyAdjustment;
	
	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	private String name;
	
	@Column(name="external_code", length=50)
	@Required
	@DisplaySize(20)
	private String externalCode;

	//bi-directional many-to-one association to Currency
	@ManyToOne
	@JoinColumn(name="currency_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="currencyId, name")
	private Currency currency;

	//bi-directional many-to-one association to GroupAccount
	@ManyToOne
	@JoinColumn(name="group_account_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="name")
	private GroupAccount groupAccount;

	public BookAccount() {
	}

	public String getBookAccountId() {
		return this.bookAccountId;
	}

	public void setBookAccountId(String bookAccountId) {
		this.bookAccountId = bookAccountId;
	}

	public String getBookAccountParent() {
		return this.bookAccountParent;
	}

	public void setBookAccountParent(String bookAccountParent) {
		this.bookAccountParent = bookAccountParent;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Types.YesNoIntegerType getMovement() {
		return movement;
	}

	public void setMovement(Types.YesNoIntegerType movement) {
		this.movement = movement;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public GroupAccount getGroupAccount() {
		return this.groupAccount;
	}

	public void setGroupAccount(GroupAccount groupAccount) {
		this.groupAccount = groupAccount;
	}

	public Types.YesNoIntegerType getAllowManualEntry() {
		return allowManualEntry;
	}

	public void setAllowManualEntry(Types.YesNoIntegerType allowManualEntry) {
		this.allowManualEntry = allowManualEntry;
	}

	public Types.YesNoIntegerType getAllowCurrencyAdjustment() {
		return allowCurrencyAdjustment;
	}

	public void setAllowCurrencyAdjustment(Types.YesNoIntegerType allowCurrencyAdjustment) {
		this.allowCurrencyAdjustment = allowCurrencyAdjustment;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

}