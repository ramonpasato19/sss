package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

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
	public static List<AccountInvoiceTax> getCalculatedAccountInvoiceTaxes(AccountInvoice invoice)
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
	
	public static void persistAccountInvoiceTaxes(AccountInvoice invoice)
	{	
		
		XPersistence.getManager().createQuery("DELETE FROM AccountInvoiceTax o "
					+ "WHERE o.accountInvoice.accountId = :accountId")
				.setParameter("accountId", invoice.getAccountId())
				.executeUpdate();

		List<AccountInvoiceTax> taxes = getCalculatedAccountInvoiceTaxes(invoice);

		for (AccountInvoiceTax tax : taxes)
			XPersistence.getManager().persist(tax);
	}
	
}
