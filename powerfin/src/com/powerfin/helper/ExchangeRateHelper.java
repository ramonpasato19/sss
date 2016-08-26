package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.Currency;

public class ExchangeRateHelper {

	public final static String CURRENCY_ADJUSTMENT_TRANSACTION_MODULE = "CURRENCYADJUSTMENT";
	
	public static BigDecimal getExchangeRate(Currency currency) throws Exception {
		return getExchangeRate(currency.getCurrencyId());
	}
	
	public static BigDecimal getExchangeRate(String currencyId) throws Exception {
		return getExchangeRate(currencyId, CompanyHelper.getCurrentAccountingDate());
	}
	
	public static BigDecimal getExchangeRate(Currency currency, Date accountingDate) throws Exception {
		return getExchangeRate(currency.getCurrencyId(), accountingDate);
	}

	public static BigDecimal getExchangeRate(String currencyId, Date accountingDate) throws Exception {
		try
		{
			ExchangeRateDaily exchangeRates = (ExchangeRateDaily)XPersistence.getManager().createQuery("select o from ExchangeRateDaily o "
					+ "WHERE o.currency.currencyId=:currencyId "
					+ "AND :accountingDate BETWEEN o.fromDate AND o.toDate "
					+ "ORDER BY toDate DESC")
					.setParameter("currencyId", currencyId)
					.setParameter("accountingDate", accountingDate)
					.getSingleResult();
			return exchangeRates.getExchangeRate();
		}
		catch(javax.persistence.NoResultException ex)
		{
			throw new InternalException("exchange_rate_not_found_for_currency",currencyId);
		}
	}
}
