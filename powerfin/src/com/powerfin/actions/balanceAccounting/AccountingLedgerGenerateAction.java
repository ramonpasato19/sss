package com.powerfin.actions.balanceAccounting;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.helper.*;

public class AccountingLedgerGenerateAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();
		
		getView().setValue("fromDate",fromDate);
		getView().setValue("toDate",toDate);
				
		getView().refreshCollections();
	}

}
