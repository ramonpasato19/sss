package com.powerfin.helper;

import java.math.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class AccountInvoiceHelper {
	
	public final static String STATUS_INVOICE_CANCEL = "003";
	public final static String STATUS_INVOICE_WITH_RETENTION = "005";
	public final static String STATUS_INVOICE_ACTIVE = "002";
	public final static String STATUS_PROCESS_FINANCIAL = "002";
	public final static String INVOICE_SALE_TRANSACTION_MODULE = "INVOICE_SALE";
	public final static String INVOICE_PURCHASE_TRANSACTION_MODULE = "INVOICE_PURCHASE";
	
	public final static String INVOICE_PURCHASE_PRODUCT_TYPE_ID = "201";
	public final static String INVOICE_SALE_PRODUCT_TYPE_ID = "102";
	
	public final static String RETENTION_PURCHASE_PRODUCTID = "103";	
	public final static String RETENTION_SALE_PRODUCTID = "203";
	
	
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
	
}
