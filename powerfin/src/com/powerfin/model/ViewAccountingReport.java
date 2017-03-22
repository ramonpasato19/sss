package com.powerfin.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@View(members="accountingDate")
public class ViewAccountingReport {

	@Column
	private Date accountingDate;
	
	public ViewAccountingReport() {
		
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

}
