package com.powerfin.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Views({
	@View(name="ViewInventoryControl",
			members=
				"all;"+
				"account;" +
				"fromDate, toDate;"
			)
})
public class ViewInventoryControl {

	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("report")
	@SearchAction(value="SearchAccount.SearchAccountItem")
	private Account account;

	@Column
	Boolean all;

	@Column
	private Date fromDate;

	@Column
	private Date toDate;
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
	public Boolean getAll() {
		return all;
	}
	public void setAll(Boolean all) {
		this.all = all;
	}

}
