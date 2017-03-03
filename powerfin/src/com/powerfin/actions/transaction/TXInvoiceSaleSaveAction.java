package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.actions.inventory.UpdateStock;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoiceSaleSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Category saleCostCategory = CategoryHelper.getSaleCostCategory();
		Category discountCategory = CategoryHelper.getDiscountCategory();
		Account account = getDebitAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal totalAverageCost = BigDecimal.ZERO;
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    	
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			detailAmount = detail.calculateAmountWithoutDiscount().setScale(2, RoundingMode.HALF_UP);
			TransactionAccount ta = null;
			
			//Invoice
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(invoice.getAccount(), detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);

			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction));
				
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue() == null)
					throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue().compareTo(BigDecimal.ZERO)<0)
					throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
				
				totalAverageCost = accountItem.getAverageValue().multiply(new BigDecimal(detail.getQuantity())).setScale(2, RoundingMode.HALF_UP);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), totalAverageCost, new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction, costCategory));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), totalAverageCost, new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction, saleCostCategory));
			}
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
			
			//Tax
			if (detail.hasTax())
			{
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(invoice.getAccount(), detail.getTaxAmount(), transaction);
				ta.setRemark(XavaResources.getString("tax_item", detail.getAccountDetail().getName()));
				transactionAccounts.add(ta);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detail.getTaxAmount(), transaction, detail.getTax().getCategory()));
			}
			
			//Discount
			if (detail.hasDiscount())
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detail.getDiscount().setScale(2, RoundingMode.HALF_UP), transaction, discountCategory));
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detail.getDiscount().setScale(2, RoundingMode.HALF_UP), transaction));
			}
		}

		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getDebitAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);

			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
					updateStock(detail.getAccountDetail(),invoice,new BigDecimal(detail.getQuantity()), detail.calculateAmount().divide(new BigDecimal(detail.getQuantity()),3,RoundingMode.HALF_UP), detail.getAmount(), invoice.getRegistrationDate());
		}
	}

	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal cost,  BigDecimal total, Date registrerDate){

		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.removeItemStock(accountItem, invoice, quantity.negate(), cost, total,registrerDate);

	}
}
