package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.openxava.jpa.XPersistence;
import com.powerfin.helper.StockHelper;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountItem;
import com.powerfin.model.Stock;

public class UpdateStock {

	public void updateItemStock(AccountItem accountItem, AccountInvoice accountInvoice, BigDecimal quantity, BigDecimal value, BigDecimal total, Date registrerDate) {
		Stock stock=new Stock();
		stock.setAccountId(accountItem);
		stock.setAccountInvoiceId(accountInvoice);
		stock.setQuantity(quantity);
		stock.setValue(value);
		stock.setTotalValue(total);
		stock.setRegistrerDate(new Date());
		BigDecimal averageCalculate=calculeAverageValue(accountItem,value, quantity);
		stock.setAverageValue(averageCalculate);
		StockHelper.createStock(stock);
		accountItem.setAverageValue(averageCalculate);
		accountItem.setCost(value);
		XPersistence.getManager().merge(accountItem);

	}

	@SuppressWarnings("unchecked")
	private BigDecimal calculeAverageValue(AccountItem accountItem, BigDecimal newCost, BigDecimal newQuantity)	{
		List<Stock> stocks = XPersistence.getManager()
				.createQuery("select st from Stock st where st.accountId.accountId=:accountItemId")
				.setParameter("accountItemId", accountItem.getAccountId())
				.getResultList();
		BigDecimal quantityAve=BigDecimal.ZERO;
		BigDecimal valueUnit=BigDecimal.ZERO;
		BigDecimal valueTot=BigDecimal.ZERO;
		for (Stock st : stocks){
			valueUnit=st.getQuantity().multiply(st.getAverageValue());
			valueTot=valueTot.add(valueUnit);
			quantityAve=quantityAve.add(st.getQuantity());
		}
		valueTot=valueTot.add(newCost.multiply(newQuantity));
		quantityAve=quantityAve.add(newQuantity);
		BigDecimal valueAverage=valueTot.divide(quantityAve, 3, RoundingMode.HALF_UP);
		return valueAverage;
	}

	public void removeItemStock(AccountItem accountItem, AccountInvoice accountInvoice, BigDecimal quantity, BigDecimal value, BigDecimal total, Date registrerDate) {
		Stock stock=new Stock();
		stock.setAccountId(accountItem);
		stock.setAccountInvoiceId(accountInvoice);
		stock.setValue(value);
		stock.setQuantity(quantity);
		stock.setTotalValue(total);
		stock.setRegistrerDate(registrerDate);
		stock.setAverageValue(accountItem.getAverageValue());
		StockHelper.createStock(stock);

	}


}
