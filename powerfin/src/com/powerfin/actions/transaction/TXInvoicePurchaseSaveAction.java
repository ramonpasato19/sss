package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.actions.inventory.UpdateStock;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoicePurchaseSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Account account = getCreditAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Unity unity = null;
		
		account = invoice.getAccountModified()!=null?invoice.getAccountModified().getAccount():account;
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");

		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			unity = detail.getUnity()==null?transaction.getOrigenUnity():detail.getUnity();
			detailAmount = detail.calculateAmount().setScale(2, RoundingMode.HALF_UP);
			TransactionAccount ta = null;
			
			//Invoice
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);
			
			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, costCategory));
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
			
			//Tax
			if (detail.hasTax())
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, detail.getTaxAmount(), transaction);
				ta.setRemark(XavaResources.getString("tax_item", detail.getAccountDetail().getName()));
				transactionAccounts.add(ta);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, detail.getTaxAmount(), transaction, detail.getTax().getCategory()));
			}
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
					updateStock(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getAmount(), invoice.getRegistrationDate());
		}
	}
	
	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal amount,  Date registrerDate) throws Exception 
	{
		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.updateItemStock(accountItem, invoice, quantity, amount.divide(quantity, 4, RoundingMode.HALF_UP), registrerDate);
	}
}
