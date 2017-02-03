package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.actions.inventory.UpdateStock;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoicePurchaseSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getCreditAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    	
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(account, invoice.getTotal(),BigDecimal.ZERO,null, transaction));
		
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detail.getAmount(),new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction));
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(invoice.getAccount(), detail.getTaxAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction, detail.getTax().getCategory()));
		}
		
		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getCreditAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
					updateStock(detail.getAccountDetail(),invoice,new BigDecimal(detail.getQuantity()), detail.getAmount().divide(new BigDecimal(detail.getQuantity())), detail.getAmount(), invoice.getRegistrationDate());
		}
	}
	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal cost,  BigDecimal total, Date registrerDate){

		AccountItem accountItem=(AccountItem) XPersistence.getManager()
				.createQuery("from AccountItem where account.accountId='"+item.getAccountId()+"'").getSingleResult();
		UpdateStock update=new UpdateStock();
		update.updateItemStock(accountItem, invoice, quantity, cost, total,registrerDate);

	}
}
