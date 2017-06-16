package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.openxava.jpa.XPersistence;

import com.powerfin.helper.*;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountItem;
import com.powerfin.model.Stock;

public class UpdateStock {

	public void updateItemStock(AccountItem accountItem, AccountInvoice accountInvoice, BigDecimal quantity, BigDecimal value, Date registrerDate) {
		Stock stock=new Stock();
		stock.setAccountId(accountItem);
		stock.setAccountInvoiceId(accountInvoice);
		stock.setQuantity(quantity);
		stock.setValue(value);
		stock.setTotalValue(value.multiply(quantity).setScale(4, RoundingMode.HALF_UP));
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
		BigDecimal quantitySale=BigDecimal.ZERO;
		
		for (Stock st : stocks)
			if(st.getAccountInvoiceId().getProduct().getProductType().getProductTypeId().equals(AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID))
				quantitySale=quantitySale.add(st.getQuantity().abs());
		
		BigDecimal quantityNow=BigDecimal.ZERO; 
		for (Stock st : stocks){
			if(st.getAccountInvoiceId().getProduct().getProductType().getProductTypeId().equals(AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID)){
				if(quantitySale.compareTo(st.getQuantity())>0){
					quantitySale=quantitySale.subtract(st.getQuantity());
				}else{
					if(quantitySale.compareTo(BigDecimal.ZERO)==0){
						quantityNow=st.getQuantity();
					}else{
						quantityNow=st.getQuantity().subtract(quantitySale);
						quantitySale=BigDecimal.ZERO;
					}
					valueUnit=quantityNow.multiply(st.getValue());
					valueTot=valueTot.add(valueUnit);
					quantityAve=quantityAve.add(quantityNow);
				}
			}
		}
		valueTot=valueTot.add(newCost.multiply(newQuantity));
		quantityAve=quantityAve.add(newQuantity);
		BigDecimal valueAverage=valueTot.divide(quantityAve, 4, RoundingMode.HALF_UP);
		return valueAverage;
	}

	public void removeItemStock(AccountItem accountItem, AccountInvoice accountInvoice, BigDecimal quantity, BigDecimal value, Date registrerDate) {
		Stock stock=new Stock();
		stock.setAccountId(accountItem);
		stock.setAccountInvoiceId(accountInvoice);
		stock.setValue(value);
		stock.setQuantity(quantity.negate());
		stock.setTotalValue(value.multiply(quantity.negate()).setScale(4, RoundingMode.HALF_UP));
		stock.setRegistrerDate(new Date());
		stock.setAverageValue(accountItem.getAverageValue());
		StockHelper.createStock(stock);

	}


}
