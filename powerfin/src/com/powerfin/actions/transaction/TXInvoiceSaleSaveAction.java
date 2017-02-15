package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.actions.inventory.UpdateStock;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoiceSaleSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Category saleCostCategory = CategoryHelper.getSaleCostCategory();
		Account account = getDebitAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal totalAverageCost = BigDecimal.ZERO;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    	
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, detail.getFinalAmount(), transaction));
			
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detail.getAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction));
				
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue() == null)
					throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue().compareTo(BigDecimal.ZERO)<=0)
					throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
				
				totalAverageCost = accountItem.getAverageValue().multiply(new BigDecimal(detail.getQuantity())).setScale(2, RoundingMode.HALF_UP);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), totalAverageCost, new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction, costCategory));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), totalAverageCost, new BigDecimal(detail.getQuantity()), transaction.getUnityDetail(), transaction, saleCostCategory));
			}
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detail.getAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction));
			
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detail.getTaxAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction, detail.getTax().getCategory()));
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
					updateStock(detail.getAccountDetail(),invoice,new BigDecimal(detail.getQuantity()), detail.getAmount().divide(new BigDecimal(detail.getQuantity())), detail.getAmount(), invoice.getRegistrationDate());
		}
	}

	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal cost,  BigDecimal total, Date registrerDate){

		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.removeItemStock(accountItem, invoice, quantity.negate(), cost, total,registrerDate);

	}
}
