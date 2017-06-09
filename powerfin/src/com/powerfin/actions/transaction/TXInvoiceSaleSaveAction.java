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
		Account account = getDebitAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal totalAverageCost = BigDecimal.ZERO;
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Unity unity = null;
		TransactionAccount ta = null;
		account = invoice.getAccountModified()!=null?invoice.getAccountModified().getAccount():account;
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    	
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			unity = detail.getUnity()==null?transaction.getOrigenUnity():detail.getUnity();
			detailAmount = detail.calculateAmount().setScale(2, RoundingMode.HALF_UP);
			
			//Invoice
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);

			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction));
				
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue() == null)
					throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue().compareTo(BigDecimal.ZERO)<0)
					throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
				
				totalAverageCost = accountItem.getAverageValue().multiply(detail.getQuantity()).setScale(2, RoundingMode.HALF_UP);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, costCategory));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, saleCostCategory));
			}
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
		}
		
		//Taxes
		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(invoice);
		
		for (AccountInvoiceTax tax : taxes)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, tax.getTaxAmount(), transaction);
			ta.setRemark(XavaResources.getString("tax_item", tax.getTax().getName()));
			transactionAccounts.add(ta);
			
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(account, tax.getTaxAmount(), transaction, tax.getTax().getCategory()));
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
					updateStock(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getAmount(), invoice.getRegistrationDate());
			
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
		}
	}

	public void updateStock(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal amount,  Date registrerDate)
	{
		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.removeItemStock(accountItem, invoice, quantity, amount.divide(quantity, 4, RoundingMode.HALF_UP), registrerDate);
	}
}
