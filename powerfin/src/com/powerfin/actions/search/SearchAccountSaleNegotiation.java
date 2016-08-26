package com.powerfin.actions.search;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class SearchAccountSaleNegotiation extends ReferenceSearchAction { 
	
	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {
		
		Map keyValues = null;
		Account account = null;
		keyValues = getView().getRoot().getSubview("debitAccount").getKeyValuesWithValue();
		try
		{
			account = (Account)
					MapFacade.findEntity(getView().getRoot().getSubview("debitAccount").getModelName(), keyValues);
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("loan_account_is_required");
		}
		
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getSaleNegotiation().getNegotiationId());
		
		super.execute();
		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		
		getTab().setBaseCondition("${accountId} = '"+n.getDebitCreditAccount().getAccountId()+"'");	
				
	}

}