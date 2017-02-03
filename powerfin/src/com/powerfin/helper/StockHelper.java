package com.powerfin.helper;

import java.math.BigDecimal;
import java.util.Date;

import org.openxava.jpa.XPersistence;

import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountItem;
import com.powerfin.model.Stock;

public class StockHelper {

	public static Stock createStock(AccountItem accountId, AccountInvoice accountInvoiceId,
			Date registrerDate,	BigDecimal quantity,
			BigDecimal value, BigDecimal averageValue,
			BigDecimal totalValue){
		Stock s=new Stock();
		s.setAccountId(accountId);
		s.setAccountInvoiceId(accountInvoiceId);
		s.setRegistrerDate(registrerDate);
		s.setQuantity(quantity);
		s.setValue(value);
		s.setAverageValue(averageValue);
		s.setTotalValue(totalValue);
		XPersistence.getManager().persist(s);
		return s;
	}
	public static Stock createStock(Stock stock){
		return createStock(stock.getAccountId(), stock.getAccountInvoiceId(),
				stock.getRegistrerDate(), stock.getQuantity(),
				stock.getValue(),stock.getAverageValue(),
				stock.getTotalValue());
	}
}
