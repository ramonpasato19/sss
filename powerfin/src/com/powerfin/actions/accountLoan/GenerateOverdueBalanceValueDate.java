package com.powerfin.actions.accountLoan;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class GenerateOverdueBalanceValueDate extends ViewBaseAction {

	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws Exception {
		Map keyValues = null;
		Account account = null;
		keyValues = getView().getRoot().getSubview("creditAccount").getKeyValuesWithValue();
		Date accountingDate = null;
		try
		{
			account = (Account)
					MapFacade.findEntity(getView().getRoot().getSubview("creditAccount").getModelName(), keyValues);
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("account_is_required");
		}
		
		BigDecimal totalOverdueBalance = BigDecimal.ZERO;
		
		accountingDate = (Date)getView().getRoot().getValue("valueDate");
		if (accountingDate==null)
		{
			accountingDate = CompanyHelper.getCurrentAccountingDate();
			getView().getRoot().setValue("valueDate",accountingDate);
		}
		
		//Obtain list overdue balances		
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalances(account, accountingDate, false);
		
		for (AccountOverdueBalance overdueBalance:overdueBalances)
			totalOverdueBalance=totalOverdueBalance.add(overdueBalance.getTotal());
		
		getView().getRoot().getSubview("creditAccount").setValue("totalOverdueBalance", totalOverdueBalance);
		getView().getRoot().setValue("value", totalOverdueBalance);
		
		getView().refreshCollections();
		getView().getRoot().getSubview("creditAccount").setHidden("accountOverdueBalances", false);
		addMessage("overdue_balances_generated");	
	}

}
