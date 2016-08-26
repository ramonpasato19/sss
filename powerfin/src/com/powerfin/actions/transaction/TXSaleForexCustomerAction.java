package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXSaleForexCustomerAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		//official account payable
		Account debitAccount = getDebitAccount();
		
		//foreign account payable
		Account creditAccount = getCreditAccount();

		BigDecimal value = getValue();
		BigDecimal officialValue = null;
		BigDecimal transactionOfficialValue = null;
		BigDecimal position = null;
		BigDecimal transactionExchangeRate = getExchangeRate();
		BigDecimal currentExchangeRate = ExchangeRateHelper.getExchangeRate(getCurrencyId());
		
		officialValue = value.multiply(currentExchangeRate).setScale(2, RoundingMode.HALF_UP);
		transactionOfficialValue = value.multiply(transactionExchangeRate).setScale(2, RoundingMode.HALF_UP);
		position = transactionOfficialValue.subtract(officialValue);

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, transactionOfficialValue, transaction));
		//negative position=expense
		//positive position=income
		if (position.compareTo(BigDecimal.ZERO)>0)
			transactionAccounts.add(TransactionAccountHelper
					.createCustomCreditTransactionAccount(debitAccount, position.abs(), transaction, CategoryHelper.getInForexCategory()));
		else
			transactionAccounts.add(TransactionAccountHelper
					.createCustomDebitTransactionAccount(debitAccount, position.abs(), transaction, CategoryHelper.getExForexCategory()));
		
		return transactionAccounts;
	}
}
