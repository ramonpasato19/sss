package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.actions.inventory.*;
import com.powerfin.exception.*;
import com.powerfin.model.*;

public class AccountInvoiceHelper {
	
	public final static String STATUS_INVOICE_CANCEL = "003";
	public final static String STATUS_INVOICE_WITH_RETENTION = "005";
	public final static String STATUS_INVOICE_REQUEST = "001";
	public final static String STATUS_INVOICE_ACTIVE = "002";
	public final static String STATUS_PROCESS_FINANCIAL = "002";
	
	public final static String STATUS_RETENTION_REQUEST = "001";
	public final static String STATUS_RETENTION_ACTIVE = "002";
	
	public final static String INVOICE_PURCHASE_PRODUCT_TYPE_ID = "201";
	public final static String INVOICE_SALE_PRODUCT_TYPE_ID = "102";
	
	public final static String RETENTION_PURCHASE_PRODUCT_TYPE_ID = "105";
	public final static String RETENTION_SALE_PRODUCT_TYPE_ID = "205";
	
	public final static String CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID = "106";
	public final static String CREDIT_NOTE_SALE_PRODUCT_TYPE_ID = "206";
	
	public final static String ORDER_PURCHASE_PRODUCT_TYPE_ID = "207";
	
	public static boolean cancelInvoice(Account account) throws Exception {
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal balance = accountInvoice.getBalance();
		
		if (balance==null)
			throw new InternalException("null_balance_for_invoice", account.getAccountId());
		if (balance.compareTo(BigDecimal.ZERO)==0)
		{
			account.setAccountStatus(AccountStatusHelper.getAccountStatus(STATUS_INVOICE_CANCEL));
			AccountHelper.updateAccount(account);
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountInvoiceTax> getCalculatedAccountInvoiceTaxes(AccountInvoice invoice) throws Exception
	{
		String query = "SELECT  "
				+ "t, "
				+ "sum(o.amount) "
				+ "FROM AccountInvoiceDetail o JOIN o.tax t "
				+ "WHERE o.taxPercentage>0 "
				+ "AND o.accountInvoice.accountId = :invoice "
				+ "GROUP BY t"; 
		
		List<Object[]> calculatedTaxes = XPersistence.getManager()
				.createQuery(query)
			.setParameter("invoice", invoice.getAccountId())
			.getResultList();
		
		List<AccountInvoiceTax> taxes = new ArrayList<AccountInvoiceTax>();
		for (Object[] tax : calculatedTaxes)
		{
			AccountInvoiceTax accountInvoiceTax = new AccountInvoiceTax((Tax)tax[0], (BigDecimal)tax[1]);
			accountInvoiceTax.setAccountInvoice(invoice);
			taxes.add(accountInvoiceTax);
		}
		return taxes;
	}
	
	public static void persistAccountInvoiceTaxes(AccountInvoice invoice) throws Exception
	{	
		
		XPersistence.getManager().createQuery("DELETE FROM AccountInvoiceTax o "
					+ "WHERE o.accountInvoice.accountId = :accountId")
				.setParameter("accountId", invoice.getAccountId())
				.executeUpdate();

		List<AccountInvoiceTax> taxes = getCalculatedAccountInvoiceTaxes(invoice);

		for (AccountInvoiceTax tax : taxes)
			XPersistence.getManager().persist(tax);
	}
	
	public static List<TransactionAccount> getTransactionAccountsForInvoiceSale(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Category saleCostCategory = CategoryHelper.getSaleCostCategory();
		Account account = transaction.getDebitAccount();
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
			if (detail.getUnity()!=null)
				unity = detail.getUnity();
			else if (transaction.getOrigenUnity()!=null && unity == null)
				unity = transaction.getOrigenUnity();
			else if (unity == null)
				unity = CompanyHelper.getDefaultUnity();
			
			detailAmount = detail.calculateAmount().setScale(2, RoundingMode.HALF_UP);
			
			//Invoice
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);

			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
			{
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, CategoryHelper.getBalanceCategory(), account.getBranch()));
				
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue() == null)
					throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
				if (accountItem.getAverageValue().compareTo(BigDecimal.ZERO)<0)
					throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
				
				totalAverageCost = accountItem.getAverageValue().multiply(detail.getQuantity()).setScale(2, RoundingMode.HALF_UP);
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, costCategory, account.getBranch()));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, saleCostCategory, account.getBranch()));
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
	
	public static void postInvoiceSaleSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getDebitAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
					updateStockInvoiceSale(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getUnitPrice(), invoice.getRegistrationDate());
			
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
		}
	}

	public static boolean postInvoiceSalePaymentSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
			return AccountInvoiceHelper.cancelInvoice(transaction.getCreditAccount());
		return false;
	}
	
	public static void updateStockInvoiceSale(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal amount,  Date registrerDate)
	{
		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.removeItemStock(accountItem, invoice, quantity, amount, registrerDate);
	}
}
