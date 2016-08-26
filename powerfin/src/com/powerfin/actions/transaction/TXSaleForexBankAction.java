package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXSaleForexBankAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		//foreign account bank
		Account debitAccount = getDebitAccount();
		
		//official account bank
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
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(creditAccount, transactionOfficialValue, transaction));
		//negative position=expense
		//positive position=income
		if (position.compareTo(BigDecimal.ZERO)>0)
			transactionAccounts.add(TransactionAccountHelper
					.createCustomCreditTransactionAccount(creditAccount, position.abs(), transaction, CategoryHelper.getInForexCategory()));
		else
			transactionAccounts.add(TransactionAccountHelper
					.createCustomDebitTransactionAccount(creditAccount, position.abs(), transaction, CategoryHelper.getExForexCategory()));
		
		return transactionAccounts;
	}
}
