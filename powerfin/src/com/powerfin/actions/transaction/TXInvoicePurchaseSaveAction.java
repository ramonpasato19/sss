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
		Category discountCategory = CategoryHelper.getDiscountCategory();
		Account account = getCreditAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    			
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			detailAmount = detail.calculateAmountWithoutDiscount().setScale(2, RoundingMode.HALF_UP);
			TransactionAccount ta = null;
			
			//Invoice
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);
			
			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction, costCategory));
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
			
			//Tax
			if (detail.hasTax())
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detail.getTaxAmount(), transaction);
				ta.setRemark(XavaResources.getString("tax_item", detail.getAccountDetail().getName()));
				transactionAccounts.add(ta);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(invoice.getAccount(), detail.getTaxAmount(), transaction, detail.getTax().getCategory()));
			}
			
			//Discount
			if (detail.hasDiscount())
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detail.getDiscount().setScale(2, RoundingMode.HALF_UP), transaction, discountCategory));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(invoice.getAccount(), detail.getDiscount().setScale(2, RoundingMode.HALF_UP), transaction));
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
					updateStock(detail.getAccountDetail(),invoice,new BigDecimal(detail.getQuantity()), detail.calculateAmount().divide(new BigDecimal(detail.getQuantity()),3,RoundingMode.HALF_UP), detail.getAmount(), invoice.getRegistrationDate());
		}
	}
	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal cost,  BigDecimal total, Date registrerDate) throws Exception 
	{

		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.updateItemStock(accountItem, invoice, quantity, cost, total,registrerDate);

	}
}
