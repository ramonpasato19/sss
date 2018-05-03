package com.powerfin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.View;

@View(members="bookAccount;"
		+ "projectedAccountingDate;")
public class ViewAccountingReportByBookAccount {

	@Column
	private Date projectedAccountingDate;
	
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchAction("SearchBookAccount.SearchMovementBookAccount")
	private BookAccount bookAccount;


	public ViewAccountingReportByBookAccount() {
		
	}

	public BookAccount getBookAccount() {
		return bookAccount;
	}

	public void setBookAccount(BookAccount bookAccount) {
		this.bookAccount = bookAccount;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

}
