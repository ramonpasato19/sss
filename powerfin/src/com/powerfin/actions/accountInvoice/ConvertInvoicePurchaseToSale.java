package com.powerfin.actions.accountInvoice;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.model.*;

public class ConvertInvoicePurchaseToSale extends SaveAction{

	public void execute() throws Exception {
		
		// Tomo la factura de compra que se necesita
		AccountInvoice accountInvoicePurchase=XPersistence.getManager().find(AccountInvoice.class, getView().getValueString("invoices.accountId"));
		// Tomo la nueva factura de venta 
		AccountInvoice accountInvoice=XPersistence.getManager().find(AccountInvoice.class, getView().getValueString("accountId"));
		
		// Creacion de los detalles
		List <AccountInvoiceDetail> invoiceDetails=accountInvoicePurchase.getDetails();
		BigDecimal unitPrice=BigDecimal.ZERO;
		BigDecimal newAmount=BigDecimal.ZERO;
		BigDecimal newTax=BigDecimal.ZERO;
		for(AccountInvoiceDetail accountDetail:invoiceDetails){
			unitPrice=BigDecimal.ZERO;
			newAmount=BigDecimal.ZERO;
			newTax=BigDecimal.ZERO;
			AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
			accountInvoiceDetail.setAccountInvoice(accountInvoice);
			accountInvoiceDetail.setAccountDetail(accountDetail.getAccountDetail());
			accountInvoiceDetail.setTax(accountDetail.getTax());
			accountInvoiceDetail.setQuantity(accountDetail.getQuantity());
			unitPrice=accountDetail.getUnitPrice().add(accountDetail.getUnitPrice().multiply(new BigDecimal(10)).divide(new BigDecimal(100))).setScale(4, RoundingMode.HALF_UP) ;
			accountInvoiceDetail.setUnitPrice(unitPrice);
			accountInvoiceDetail.setDiscount(accountDetail.getDiscount());
			newAmount=unitPrice.multiply(accountDetail.getQuantity()).setScale(4, RoundingMode.HALF_UP);
			accountInvoiceDetail.setAmount(newAmount);
			if(accountDetail.getTaxAmount().compareTo(BigDecimal.ZERO)>0){
				newTax=unitPrice.multiply(accountDetail.getTax().getPercentage()).divide(new BigDecimal(100)).setScale(4, RoundingMode.HALF_UP);
			}
			accountInvoiceDetail.setTaxAmount(newTax);
			accountInvoiceDetail.setFinalAmount(newAmount.add(newTax).setScale(4, RoundingMode.HALF_UP));
			XPersistence.getManager().persist(accountInvoiceDetail);
		}
	}
}
