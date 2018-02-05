package com.powerfin.actions.accountLoan.purchasePortfolio;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class GenerateOverdueBalanceForConsult extends ViewBaseAction {

	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws Exception {
		Map keyValues = null;
		AccountLoan account = null;
		keyValues = getView().getRoot().getKeyValuesWithValue();
		Date accountingDate = null;
		try
		{
			account = (AccountLoan)
					MapFacade.findEntity(getView().getRoot().getModelName(), keyValues);
			
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("account_is_required");
		}
			
		accountingDate = (Date)getView().getRoot().getValue("projectedAccountingDate");
		if (accountingDate==null)
		{
			accountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("projectedAccountingDate",accountingDate);
		}

		//Obtain list overdue balances
		AccountLoanHelper.generateOverdueBalances(account.getAccount(), accountingDate, false);
			
		getView().refreshCollections();
		
		addMessage("overdue_balances_generated");
	}

}
