package com.powerfin.actions.company;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;

public class AccountingClosingDayAction extends SaveAction {
	
	public void execute() throws Exception {
		super.execute();
		BalanceAccountingHelper.generateBalanceSheet(CompanyHelper.getCurrentAccountingDate());
		XPersistence.commit();
	}

}
