package com.powerfin.model;

import java.util.Date;
import javax.persistence.Column;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Views({
	@View(name="ViewInventoryControl",
			members=
			  "fromDate, toDate;"
			)
})
public class ViewInventoryControl {
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


}
