package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXPurchaseForexCustomerAction extends TXSaveAction {

	public void extraValidations() throws Exception {
		super.extraValidations();
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		//foreign account payable
		Account debitAccount = getDebitAccount();
		
		//official account payable
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
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, transactionOfficialValue, transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));
		//negative position=income
		//positive position=expense
		if (position.compareTo(BigDecimal.ZERO)>0)
			transactionAccounts.add(TransactionAccountHelper
					.createCustomDebitTransactionAccount(creditAccount, position.abs(), transaction, CategoryHelper.getExForexCategory()));
		else
			transactionAccounts.add(TransactionAccountHelper
					.createCustomCreditTransactionAccount(creditAccount, position.abs(), transaction, CategoryHelper.getInForexCategory()));
		
		return transactionAccounts;
	}
}
