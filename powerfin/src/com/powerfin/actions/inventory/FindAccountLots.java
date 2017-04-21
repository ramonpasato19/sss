package com.powerfin.actions.inventory;

import java.util.Calendar;
import java.util.Date;
import org.openxava.actions.ViewBaseAction;
import com.powerfin.helper.CompanyHelper;

public class FindAccountLots extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Date fromDate = (Date) getView().getValue("fromDate");
		Date toDate = (Date) getView().getValue("toDate");
		if (fromDate == null) {
			fromDate = CompanyHelper.getCurrentAccountingDate();
			Calendar cFromDate = Calendar.getInstance();
			cFromDate.setTime(fromDate);
			cFromDate.set(Calendar.DAY_OF_MONTH, 1);
			fromDate = cFromDate.getTime();
		}
		if (toDate == null)
			toDate = CompanyHelper.getCurrentAccountingDate();
		getView().setValue("fromDate", fromDate);
		getView().setValue("toDate", toDate);
		getView().refreshCollections();
	}
	
}