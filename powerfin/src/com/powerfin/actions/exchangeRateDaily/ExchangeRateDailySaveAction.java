package com.powerfin.actions.exchangeRateDaily;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.Currency;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;
import com.powerfin.util.*;

public class ExchangeRateDailySaveAction extends SaveAction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws Exception {
	
		Map<String, String> mapCurrency = (Map<String, String>) getView().getValue("currency");
		String currencyId = (String)mapCurrency.get("currencyId");
		Currency officialCurrency = CompanyHelper.getDefaultCurrency();
		Date toDate = (Date)getView().getValue("toDate");
		Date fromDate = (Date)getView().getValue("fromDate");
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		BigDecimal position = BigDecimal.ZERO;
		
		List<ExchangeRateDaily> exchangeRates = (List<ExchangeRateDaily>) XPersistence.getManager().createQuery("SELECT o "
				+ "FROM ExchangeRateDaily o "
				+ "WHERE o.currency.currencyId = :currencyId "
				+ "AND o.fromDate > :accountingDate")
				.setParameter("currencyId", currencyId)
				.setParameter("accountingDate", accountingDate)
				.getResultList();
		
		if (exchangeRates!=null && !exchangeRates.isEmpty())
		{
			if (exchangeRates.size()>0)
				throw new InternalException("exists_exchange_rate_with_greater_accounting_date",accountingDate);
		}
		
		if (getView().isKeyEditable()) { //Create 
			
			exchangeRates = (List<ExchangeRateDaily>) XPersistence.getManager().createQuery("SELECT o "
					+ "FROM ExchangeRateDaily o "
					+ "WHERE o.currency.currencyId = :currencyId "
					+ "AND fromDate = :accountingDate")
					.setParameter("currencyId", currencyId)
					.setParameter("accountingDate", accountingDate).getResultList();
			if (exchangeRates!=null && !exchangeRates.isEmpty())
					throw new InternalException("a_exchange_rate_already_exists_with_accounting_date", accountingDate);
			
			exchangeRates = (List<ExchangeRateDaily>) XPersistence.getManager().createQuery("SELECT o "
					+ "FROM ExchangeRateDaily o "
					+ "WHERE o.currency.currencyId = :currencyId "
					+ "and toDate = :defaultExpireDate")
					.setParameter("currencyId", currencyId)
					.setParameter("defaultExpireDate", UtilApp.DEFAULT_EXPIRY_DATE).getResultList();
			if (exchangeRates!=null && !exchangeRates.isEmpty())
			{
				if (exchangeRates.size()>1)
					throw new InternalException("multiple_exchange_rates_for_date",UtilApp.DEFAULT_EXPIRY_DATE, currencyId);
				
				ExchangeRateDaily oldExchangeRateDaily = XPersistence.getManager().find(ExchangeRateDaily.class, 
						((ExchangeRateDaily)exchangeRates.get(0)).getExchangeRateDailyId());
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(accountingDate);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				oldExchangeRateDaily.setToDate(calendar.getTime());
				
				XPersistence.getManager().merge(oldExchangeRateDaily);
			}
			getView().setValue("fromDate", accountingDate);
			getView().setValue("toDate", UtilApp.DEFAULT_EXPIRY_DATE);
		}
		else { //Modify
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(toDate);
			Calendar calendarDefaultExpireDate = Calendar.getInstance();
			calendarDefaultExpireDate.setTime(UtilApp.DEFAULT_EXPIRY_DATE);
			if (!calendar.equals(calendarDefaultExpireDate))
				throw new InternalException("not_allowed_to_modify_old_records");	
			
			calendar.setTime(fromDate);
			calendarDefaultExpireDate.setTime(accountingDate);
			if (!calendar.equals(calendarDefaultExpireDate))
				throw new InternalException("not_allowed_to_modify_records_with_diferent_accounting_date", accountingDate);	
			
		}
		
		super.execute();

		

		if (getErrors().isEmpty()) {

			Map keyValues = getView().getKeyValues();
			ExchangeRateDaily exchangeRateDaily = (ExchangeRateDaily) MapFacade
					.findEntity(getView().getModelName(), keyValues);
			String schema = XPersistence.getDefaultSchema().toLowerCase();
			
			List<Account> generalAccounts = XPersistence.getManager().createQuery("SELECT a "
					+ "FROM Account a "
					+ "WHERE a.product.currency.currencyId = :currecyId "
					+ "AND a.product.productType.productTypeId = :productTypeId ")
					.setParameter("currecyId", officialCurrency.getCurrencyId())
					.setParameter("productTypeId", "999")
					.getResultList();

			if (generalAccounts==null || generalAccounts.isEmpty())
				throw new InternalException("general_accounts_not_found", officialCurrency.getCurrencyId());
			
			Account generalAccount = generalAccounts.get(0);
			
			List<BookAccount> bookAccounts= (List<BookAccount>) XPersistence.getManager().createQuery("SELECT o "
					+ "FROM BookAccount o "
					+ "WHERE o.movement = 1 "
					+ "AND o.currency.currencyId != :officialCurrencyId "
					+ "AND o.allowCurrencyAdjustment = 1 ")
					.setParameter("officialCurrencyId", officialCurrency.getCurrencyId())
					.getResultList();
			
			if (bookAccounts!=null && !bookAccounts.isEmpty())
			{
				
				TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, ExchangeRateHelper.CURRENCY_ADJUSTMENT_TRANSACTION_MODULE);
				Transaction transaction = TransactionHelper.getNewInitTransaction();
     			transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(BigDecimal.ZERO);
     			transaction.setRemark(XavaResources.getString("currency_adjustment_transaction",exchangeRateDaily.getPreviousExchangeRate(), exchangeRateDaily.getExchangeRate()));
     			transaction.setCurrency(CompanyHelper.getDefaultCurrency());
     			
     			XPersistence.getManager().persist(transaction);
     			
     			List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
     			
				for(BookAccount bookAccount:bookAccounts)
				{
					Query query = XPersistence.getManager()
						.createNativeQuery("SELECT COALESCE(round(sum((balance*:currentExchangeRate)-(balance*:previousExchangeRate)),2),0) "
						+ "FROM "+schema+".balance b "
						+ "WHERE to_date=:defaultExpireDate "
						+ "AND book_account_id = :bookAccountId");  
					query.setParameter("previousExchangeRate", exchangeRateDaily.getPreviousExchangeRate());
					query.setParameter("currentExchangeRate", exchangeRateDaily.getExchangeRate());
					query.setParameter("defaultExpireDate", UtilApp.DEFAULT_EXPIRY_DATE);
					query.setParameter("bookAccountId", bookAccount.getBookAccountId());
					BigDecimal val = (BigDecimal) query.getSingleResult();
					System.out.println(bookAccount.getBookAccountId()+": "+val.toString());
					if (val.compareTo(BigDecimal.ZERO)!=0)
					{					
						
						List<CategoryAccount> categories = XPersistence.getManager().createQuery("SELECT o "
								+ "FROM CategoryAccount o, Account a, Product p "
								+ "WHERE o.category.categoryId = 'BALANCE' "
								+ "AND o.bookAccount = :bookAccountId "
								+ "AND a.accountId = o.account.accountId "
								+ "AND a.product.productId = p.productId "
								+ "AND p.productType.productTypeId = '000' ")
								.setParameter("bookAccountId", bookAccount.getBookAccountId())
								.getResultList();
						
						
						
						if (categories!=null && !categories.isEmpty())
						{
							if (categories.size()>1)
								throw new InternalException("multiple_accounts_for_book_accout",bookAccount.getBookAccountId());
							CategoryAccount categoryAccount = categories.get(0);
							if (bookAccount.getGroupAccount().getDebtorOrCreditor().equals(DebitOrCredit.DEBIT))
							{
								if (val.compareTo(BigDecimal.ZERO)>0)
								{
									transactionAccounts.add(TransactionAccountHelper
											.createCustomDebitTransactionAccount(categoryAccount.getAccount(), val.abs(), transaction, CategoryHelper.getBalanceCategory(), Types.YesNoIntegerType.NO, Types.YesNoIntegerType.YES));
									position = position.add(val);
								}
								else
								{
									transactionAccounts.add(TransactionAccountHelper
											.createCustomCreditTransactionAccount(categoryAccount.getAccount(), val.abs(), transaction, CategoryHelper.getBalanceCategory(), Types.YesNoIntegerType.NO, Types.YesNoIntegerType.YES));
									position = position.subtract(val);
								}
							}
							else
							{
								if (val.compareTo(BigDecimal.ZERO)>0)
								{
									transactionAccounts.add(TransactionAccountHelper
											.createCustomCreditTransactionAccount(categoryAccount.getAccount(), val.abs(), transaction, CategoryHelper.getBalanceCategory(), Types.YesNoIntegerType.NO, Types.YesNoIntegerType.YES));
									position = position.subtract(val);
								}
								else
								{
									transactionAccounts.add(TransactionAccountHelper
											.createCustomDebitTransactionAccount(categoryAccount.getAccount(), val.abs(), transaction, CategoryHelper.getBalanceCategory(), Types.YesNoIntegerType.NO, Types.YesNoIntegerType.YES));
									position = position.add(val);
								}
							}
						}
						else
							throw new InternalException("account_for_book_accout_not_found",bookAccount.getBookAccountId());
					}				
				}
				
				if (position.compareTo(BigDecimal.ZERO)>0)
					transactionAccounts.add(TransactionAccountHelper
							.createCustomCreditTransactionAccount(generalAccount, position.abs(), transaction, CategoryHelper.getInForexCategory()));
				else if (position.compareTo(BigDecimal.ZERO)<0)
					transactionAccounts.add(TransactionAccountHelper
							.createCustomDebitTransactionAccount(generalAccount, position.abs(), transaction, CategoryHelper.getExForexCategory()));
				else
					System.out.println("******NO_POSITION: "+position);
				
				for (TransactionAccount ta : transactionAccounts)
				{
					XPersistence.getManager().persist(ta);
				}
			}
		}

	}
}
