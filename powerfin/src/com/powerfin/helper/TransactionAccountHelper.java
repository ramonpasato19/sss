package com.powerfin.helper;

import java.math.*;
import java.util.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;

public class TransactionAccountHelper {

	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			BigDecimal value, Transaction transaction) throws Exception {
		return TransactionAccountHelper.createCustomCreditTransactionAccount(account, value, transaction, CategoryHelper.getBalanceCategory());
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			BigDecimal value, Transaction transaction) throws Exception {
		return TransactionAccountHelper.createCustomDebitTransactionAccount(account, value, transaction, CategoryHelper.getBalanceCategory());
	}
	
	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category) throws Exception {
		return TransactionAccountHelper.createCustomCreditTransactionAccount(account, value, transaction, category, null);
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category) throws Exception {
		return TransactionAccountHelper.createCustomDebitTransactionAccount(account, value, transaction, category, null);
	}
	
	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			int subaccount, BigDecimal value, Transaction transaction, Category category) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, subaccount, category, DebitOrCredit.CREDIT, value, transaction, null, null, null);
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			int subaccount, BigDecimal value, Transaction transaction, Category category) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, subaccount, category, DebitOrCredit.DEBIT, value, transaction, null, null, null);
	}
	
	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			int subaccount, BigDecimal value, Transaction transaction, Category category, Date dueDate) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, subaccount, category, DebitOrCredit.CREDIT, value, transaction, null, null, dueDate);
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			int subaccount, BigDecimal value, Transaction transaction, Category category, Date dueDate) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, subaccount, category, DebitOrCredit.DEBIT, value, transaction, null, null, dueDate);
	}
	
	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category, Types.YesNoIntegerType updateBalance) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, 0, category, DebitOrCredit.CREDIT, value, transaction, updateBalance, null, null);
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category, Types.YesNoIntegerType updateBalance) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, 0, category, DebitOrCredit.DEBIT, value, transaction, updateBalance, null, null);
	}
	
	public static TransactionAccount createCustomCreditTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category, Types.YesNoIntegerType updateBalance, Types.YesNoIntegerType officialValue) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, 0, category, DebitOrCredit.CREDIT, value, transaction, updateBalance, officialValue, null);
	}
	public static TransactionAccount createCustomDebitTransactionAccount(Account account,
			BigDecimal value, Transaction transaction, Category category, Types.YesNoIntegerType updateBalance, Types.YesNoIntegerType officialValue) throws Exception {
		return TransactionAccountHelper.createTransactionAccount(account, 0, category, DebitOrCredit.DEBIT, value, transaction, updateBalance, officialValue, null);
	}
			
	public static TransactionAccount createTransactionAccount(Account account,
			Integer subaccount, Category category, DebitOrCredit debitOrCredit,
			BigDecimal value, Transaction transaction, Types.YesNoIntegerType updateBalance, 
			Types.YesNoIntegerType officialValue,
			Date dueDate) throws Exception{
		TransactionAccount ta = new TransactionAccount();
		if (account == null)
			throw new InternalException("account_is_required_for_transaction", account);
		
		if (category == null)
			throw new InternalException("category_is_required_for_transaction", category);
		
		if (debitOrCredit == null)
			throw new InternalException("debitOrCredit_is_required_for_transaction", debitOrCredit);
		
		if (!debitOrCredit.equals(DebitOrCredit.DEBIT) && !debitOrCredit.equals(DebitOrCredit.CREDIT))
			throw new InternalException("incorrect_value_for_debitOrCredit_on_transaction", debitOrCredit);
		
		if (subaccount == null)
			throw new InternalException("subaccount_is_required_for_transaction", subaccount);
		
		if (subaccount != null && subaccount < 0)
			throw new InternalException("incorrect_value_for_subaccount_on_transaction", subaccount);
		
		if (value == null)
			throw new InternalException("value_is_required_for_transaction", value);
		
		if (value != null && value.compareTo(BigDecimal.ZERO)<0)
			throw new InternalException("incorrect_value_for_value_on_transaction", value);
		
		//Por defecto siempre actualiza saldo
		if (updateBalance == null)
			ta.setUpdateBalance(Types.YesNoIntegerType.YES);
		else
			ta.setUpdateBalance(updateBalance);

		//Por defecto no es valor oficial
		if (officialValue == null)
			ta.setOfficialValue(Types.YesNoIntegerType.NO);
		else
			ta.setOfficialValue(officialValue);
		
		ta.setAccount(account);
		ta.setCategory(category);
		ta.setDebitOrCredit(debitOrCredit);
		ta.setSubaccount(subaccount);
		ta.setValue(value);
		ta.setTransaction(transaction);
		ta.setDueDate(dueDate);
		return ta;
	}
}
