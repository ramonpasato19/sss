package com.powerfin.actions.company;

import org.openxava.actions.*;

import com.powerfin.helper.*;

public class AccountingClosingDayAction extends SaveAction {
	
	public void execute() throws Exception {

		BalanceAccountingHelper.generateBalanceSheet(CompanyHelper.getCurrentAccountingDate());
	
		super.execute();
		
	}

}
