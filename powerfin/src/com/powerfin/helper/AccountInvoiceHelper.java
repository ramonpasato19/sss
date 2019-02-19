package com.powerfin.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxava.jpa.XPersistence;
import org.openxava.util.XavaResources;

import com.powerfin.actions.inventory.UpdateStock;
import com.powerfin.exception.InternalException;
import com.powerfin.exception.OperativeException;
import com.powerfin.model.Account;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountInvoiceDetail;
import com.powerfin.model.AccountInvoicePayment;
import com.powerfin.model.AccountInvoiceTax;
import com.powerfin.model.AccountItem;
import com.powerfin.model.AccountItemBranch;
import com.powerfin.model.Category;
import com.powerfin.model.Tax;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.model.Unity;

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
	
	public final static String OPERATING_CONDITION_ISSUE_ELECTRONICALLY = "ELE";
	
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
				+ "sum(round("
					+ "(o.quantity*o.unitPrice) + "
					+ "(coalesce(o.taxSpecialConsumption,0)*o.quantity) - "
					+ "(coalesce(o.discount,0)*o.quantity) "
					+ ",2)) "
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
	
	public static List<TransactionAccount> getTransactionAccountsForInvoicePurchase(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Account account = transaction.getCreditAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
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
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);
			
			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, costCategory, account.getBranch()));
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
			
		}
		
		//Taxes
		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(invoice);
		
		for (AccountInvoiceTax tax : taxes)
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, tax.getTaxAmount(), transaction);
			ta.setRemark(XavaResources.getString("tax_item", tax.getTax().getName()));
			transactionAccounts.add(ta);
			
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, tax.getTaxAmount(), transaction, tax.getTax().getCategory()));
		}
		
		return transactionAccounts;
	}
	
	public static List<TransactionAccount> getTAForCreditNoteInvoicePurchase(Transaction transaction) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Account account = transaction.getCreditAccount();
		Account accountToModify = null;
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Unity unity = null;
		TransactionAccount ta = null;
		
		if (invoice.getAccountModified().getAccount() == null)
			throw new OperativeException("account_to_modify_is_required");
		
		accountToModify = invoice.getAccountModified().getAccount();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");

		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			unity = detail.getUnity()==null?transaction.getOrigenUnity():detail.getUnity();
			detailAmount = detail.calculateAmount().setScale(2, RoundingMode.HALF_UP);
			
			//Invoice
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountToModify, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);
			
			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, costCategory, accountToModify.getBranch()));
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
			
		}
		
		//Taxes
		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(invoice);
		
		for (AccountInvoiceTax tax : taxes)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountToModify, tax.getTaxAmount(), transaction);
			ta.setRemark(XavaResources.getString("tax_item", tax.getTax().getName()));
			transactionAccounts.add(ta);
			
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(accountToModify, tax.getTaxAmount(), transaction, tax.getTax().getCategory()));
		}
		
		return transactionAccounts;
	}
	
	public static List<TransactionAccount> getTAForCreditNoteInvoiceSale(Transaction transaction, Integer accountingCostOfSale) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Category saleCostCategory = CategoryHelper.getSaleCostCategory();
		Account account = transaction.getDebitAccount();
		Account accountToModify = null;
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal totalAverageCost = BigDecimal.ZERO;
		BigDecimal detailAmount = null;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Unity unity = null;
		TransactionAccount ta = null;
		
		if (invoice.getAccountModified().getAccount() == null)
			throw new OperativeException("account_to_modify_is_required");
		
		accountToModify = invoice.getAccountModified().getAccount();
		
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
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountToModify, detailAmount, transaction);
			ta.setRemark(detail.getAccountDetail().getName());
			transactionAccounts.add(ta);

			//AccountItem
			if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
			{
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, CategoryHelper.getBalanceCategory(), accountToModify.getBranch()));
				
				//Accounting Cost Of Sale
				if (accountingCostOfSale == 1)
				{
					if (accountItem.getAverageValue() == null)
						throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
					if (accountItem.getAverageValue().compareTo(BigDecimal.ZERO)<0)
						throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
					
					totalAverageCost = accountItem.getAverageValue().multiply(detail.getQuantity()).setScale(2, RoundingMode.HALF_UP);
					transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, costCategory, accountToModify.getBranch()));
					transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), totalAverageCost, detail.getQuantity(), unity, transaction, saleCostCategory, accountToModify.getBranch()));
				}
			}
			//AccountAccountant
			else
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), detailAmount, transaction));
		}
		
		//Taxes
		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(invoice);
		
		for (AccountInvoiceTax tax : taxes)
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountToModify, tax.getTaxAmount(), transaction);
			ta.setRemark(XavaResources.getString("tax_item", tax.getTax().getName()));
			transactionAccounts.add(ta);
			
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(accountToModify, tax.getTaxAmount(), transaction, tax.getTax().getCategory()));
		}

		return transactionAccounts;
	}
	
	public static void postInvoicePurchaseSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getCreditAccount();
			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				{
					//updateStockInvoicePurchase(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getCompleteUnitPrice(), invoice.getRegistrationDate());
					AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
					AccountItemHelper.updateAverageCost(accountItem, a.getBranch());
				}
			
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
		}
	}
	
	public static boolean postInvoicePurchasePaymentSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
			return AccountInvoiceHelper.cancelInvoice(transaction.getDebitAccount());
		return false;
	}
	
	public static void updateStockInvoicePurchase(Account item,AccountInvoice invoice, BigDecimal quantity, BigDecimal amount,  Date registrerDate) throws Exception 
	{
		AccountItem accountItem=(AccountItem) XPersistence.getManager().find(AccountItem.class, item.getAccountId());
		UpdateStock update=new UpdateStock();
		update.updateItemStock(accountItem, invoice, quantity, amount, registrerDate);
	}
	
	public static List<TransactionAccount> getTransactionAccountsForInvoiceSale(Transaction transaction) throws Exception
	{
		return getTransactionAccountsForInvoiceSale(transaction, 1);
	}
	public static List<TransactionAccount> getTransactionAccountsForInvoiceSale(Transaction transaction, Integer accountingCostOfSale) throws Exception
	{
		Category costCategory = CategoryHelper.getCostCategory();
		Category saleCostCategory = CategoryHelper.getSaleCostCategory();
		Account account = transaction.getDebitAccount();
		Account debitAccountForPayment = null;
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		BigDecimal detailAmount = null;
		BigDecimal paymentValue = BigDecimal.ZERO;
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		Unity unity = null;
		TransactionAccount ta = null;
		account = invoice.getAccountModified()!=null?invoice.getAccountModified().getAccount():account;
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
		
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			paymentValue = BigDecimal.ZERO;
			
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
				AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
				if (accountItem == null)
					throw new InternalException("account_item_not_found", detail.getAccountDetail().getAccountId());
				
				if (invoice.getInvoiceVoucherType().getInvoiceVoucherTypeId().equals("EX"))
					transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, CategoryHelper.getCostCategory(), account.getBranch()));
				else
				{
					//TODO Esta quemado el producto despachos para que el balance de la venta se contabilice en la categoria TRABALANCE (balance transitorio), cambiar el proceso
					
					if (account.getProduct().getProductId().equals("1022"))
						transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, CategoryHelper.getTransitoryBalanceCategory(), account.getBranch()));
					else
						transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detailAmount, detail.getQuantity(), unity, transaction, CategoryHelper.getBalanceCategory(), account.getBranch()));
					
					//Accounting Cost Of Sale
					if (accountingCostOfSale == 1)
					{
						BigDecimal branchAverageCost = null;
						for (AccountItemBranch accountItemBranch : accountItem.getAccountItemBranch())
						{
							if (accountItemBranch.getBranch().getBranchId() == account.getBranch().getBranchId())
							{
								branchAverageCost = accountItemBranch.getAverageCost();
								break;
							}
						}
						
						if (branchAverageCost == null)
							throw new OperativeException("average_cost_is_null", detail.getAccountDetail().getAccountId());
						if (branchAverageCost.compareTo(BigDecimal.ZERO)<0)
							throw new OperativeException("average_cost_is_negative", detail.getAccountDetail().getAccountId());
						
						branchAverageCost = branchAverageCost.multiply(detail.getQuantity()).setScale(2, RoundingMode.HALF_UP);
						transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), branchAverageCost, detail.getQuantity(), unity, transaction, costCategory, account.getBranch()));
						transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(detail.getAccountDetail(), branchAverageCost, detail.getQuantity(), unity, transaction, saleCostCategory, account.getBranch()));
					}
				}
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

		//Payments
		if (invoice.getAccountInvoicePayments() != null && invoice.getAccountInvoicePayments().size()>0)
		{
			for (AccountInvoicePayment payment: invoice.getAccountInvoicePayments())
			{
				paymentValue = payment.getValue();
				
				//TODO Esta quemado los metodos de pago. hay que ver una forma que sea parametrizado
				if (payment.getInvoicePaymentMethod().getInvoicePaymentMethodId().equals("001"))//cash
				{
					if (payment.getChange()!=null && payment.getChange().compareTo(BigDecimal.ZERO)>0)
						paymentValue = paymentValue.subtract(payment.getChange());
					
					debitAccountForPayment = invoice.getPos().getCashAccount();
				}
				else if (payment.getInvoicePaymentMethod().getInvoicePaymentMethodId().equals("002")) //direct credit
				{
					//nothing to do
				}
				else if (payment.getInvoicePaymentMethod().getInvoicePaymentMethodId().equals("003")) //credit card
				{
					debitAccountForPayment = invoice.getPos().getCreditCardAccount();
				}
				else if (payment.getInvoicePaymentMethod().getInvoicePaymentMethodId().equals("004")) //check
				{
					debitAccountForPayment = invoice.getPos().getCheckAccount();
				}
				else if (payment.getInvoicePaymentMethod().getInvoicePaymentMethodId().equals("005")) //discount voucher
				{
					debitAccountForPayment = PersonHelper.getDiscountVoucherAccount(account.getPerson());
				}
				
				if (debitAccountForPayment!=null)
				{
					ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, paymentValue, transaction);
					ta.setRemark(XavaResources.getString("invoice_collection_detail", account.getCode(), payment.getDetail()));
					transactionAccounts.add(ta);
					
					ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccountForPayment, paymentValue, transaction);
					ta.setRemark(XavaResources.getString("invoice_collection_detail", account.getCode(), payment.getDetail()));
					transactionAccounts.add(ta);
				}
			}
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
			
			/* Se remueve la actualizacion del stock ya que solo se debe hacer en ingreso de mercaderia
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
					updateStockInvoiceSale(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getCompleteUnitPrice(), invoice.getRegistrationDate());
			*/
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
		}
	}
	
	public static void postCreditNoteInvoiceSaleSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getDebitAccount();
			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			Account accountToModify = invoice.getAccountModified().getAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
			
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
				{
					//updateStockInvoicePurchase(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getCompleteUnitPrice(), invoice.getRegistrationDate());
					AccountItem accountItem = XPersistence.getManager().find(AccountItem.class, detail.getAccountDetail().getAccountId());
					AccountItemHelper.updateAverageCost(accountItem, accountToModify.getBranch());
				}
			
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
			
			
			BigDecimal balance = BalanceHelper.getBalance(accountToModify);
			if (balance.compareTo(BigDecimal.ZERO) == 0)
			{
				accountToModify.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_CANCEL));
				AccountHelper.updateAccount(accountToModify);
			}
		}
	}
	
	public static void postCreditNoteInvoicePurchaseSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getCreditAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
			AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, a.getAccountId());
			
			/* Se remueve la actualizacion del stock ya que solo se debe hacer en ingreso de mercaderia
			for (AccountInvoiceDetail detail: invoice.getDetails())
				if (detail.getAccountDetail().getProduct().getProductType().getProductTypeId().equals(AccountItemHelper.ACCOUNT_ITEM_PRODUCT_TYPE))
					updateStockInvoiceSale(detail.getAccountDetail(), invoice, detail.getQuantity(), detail.getCompleteUnitPrice(), invoice.getRegistrationDate());
			*/
			
			AccountInvoiceHelper.persistAccountInvoiceTaxes(invoice);
			
			Account accountToModify = invoice.getAccountModified().getAccount();
			BigDecimal balance = BalanceHelper.getBalance(accountToModify);
			if (balance.compareTo(BigDecimal.ZERO) == 0)
			{
				accountToModify.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_CANCEL));
				AccountHelper.updateAccount(accountToModify);
			}
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
	
	public static BigDecimal getCalculateTaxes(AccountInvoice accountInvoice) throws Exception
	{
		BigDecimal value = BigDecimal.ZERO;

		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(accountInvoice);

		for (AccountInvoiceTax tax : taxes)
			value = value.add(tax.getTaxAmount());
		
		return value;
	}
	
	public static BigDecimal getTaxes(AccountInvoice accountInvoice) throws Exception
	{
		BigDecimal value = BigDecimal.ZERO;
		if(accountInvoice.getAccountInvoiceTaxes()!=null && !accountInvoice.getAccountInvoiceTaxes().isEmpty())
		{
			for (AccountInvoiceTax tax: accountInvoice.getAccountInvoiceTaxes()) {
				if (tax.getTaxAmount()!=null)
					value = value.add(tax.getTaxAmount());
			}
		}
		return value;
	}
	
	public static BigDecimal getSubtotal(AccountInvoice accountInvoice) throws Exception {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: accountInvoice.getDetails()) {
			value = value.add(detail.getAmount());
		}
		return value;
	}

	public static BigDecimal getDiscount(AccountInvoice accountInvoice) throws Exception {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: accountInvoice.getDetails()) {
			value = value.add(detail.getDiscount());
		}
		return value;
	}
}
