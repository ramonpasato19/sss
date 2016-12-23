package com.powerfin.actions.accountLoan.purchasePortfolio;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class GenerateOverdueBalance extends ViewBaseAction {

	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws Exception {
		Map keyValues = null;
		Account account = null;
		keyValues = getView().getRoot().getSubview("creditAccount").getKeyValuesWithValue();
		try
		{
			account = (Account)
					MapFacade.findEntity(getView().getRoot().getSubview("creditAccount").getModelName(), keyValues);
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("account_is_required");
		}
		
		BigDecimal totalOverdueBalance = BigDecimal.ZERO;
		
		//Obtain list overdue balances
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalances(account);
		
		for (AccountOverdueBalance overdueBalance:overdueBalances)
			totalOverdueBalance=totalOverdueBalance.add(overdueBalance.getTotal());
		
		getView().getRoot().getSubview("creditAccount").setValue("totalOverdueBalance", totalOverdueBalance);
		getView().getRoot().setValue("value", totalOverdueBalance);
		
		getView().refreshCollections();
		getView().getRoot().getSubview("creditAccount").setHidden("accountOverdueBalances", false);
		addMessage("overdue_balances_generated");	
	}

}
