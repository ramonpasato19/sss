package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;

import com.powerfin.helper.*;

public class OnModifyTransactionCurrency extends OnChangePropertyBaseAction {

	private static Log log = LogFactory.getLog(OnModifyTransactionCurrency.class);
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		getView().setEditable("currency", true);		
		try
		{
			getView().getRoot().getSubview("creditAccount").clear();
		}catch (org.openxava.util.ElementNotFoundException ex)
		{	
			log.warn("Subview CREDITACCOUNT not found to clear.");
		}
		try
		{
			getView().getRoot().getSubview("debitAccount").clear();
		}catch (org.openxava.util.ElementNotFoundException ex)
		{
			log.warn("Subview DEBITACCOUNT not found to clear.");
		}
		try
		{
			getView().getRoot().setValue("value", null);
		}catch (org.openxava.util.XavaException ex)
		{
			log.warn("Field VALUE not found to clear.");
		}

		if (getNewValue() == null) {
			return;
		}
		
		
		Map<String, String> mapCurrency = (Map<String, String>) getView().getValue("currency");
		System.out.println(mapCurrency);
		System.out.println(mapCurrency.get("currencyId"));
		BigDecimal exchangeRate = ExchangeRateHelper.getExchangeRate((String)mapCurrency.get("currencyId"));
		
		try
		{
			getView().setValue("exchangeRate", exchangeRate);
		}catch (org.openxava.util.XavaException ex)
		{
			log.warn("Field EXCHANGERATE not found.");
		}
		getView().setEditable("currency", false);
	}

}
