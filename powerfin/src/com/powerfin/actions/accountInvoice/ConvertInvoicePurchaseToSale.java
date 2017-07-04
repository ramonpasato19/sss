package com.powerfin.actions.accountInvoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.helper.*;
import com.powerfin.model.Account;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountInvoiceDetail;
import com.powerfin.model.InvoiceVoucherType;

public class ConvertInvoicePurchaseToSale extends SaveAction{

	public void execute() throws Exception {
		AccountInvoice accountInvoicePurchase=XPersistence.getManager().find(AccountInvoice.class, getView().getValueString("accountId"));

		String externalCode = "";
		
		// Crea la cuenta
		Account account = AccountHelper.createAccount(AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID, accountInvoicePurchase.getPerson().getPersonId(), AccountInvoiceHelper.STATUS_INVOICE_REQUEST, null, externalCode, accountInvoicePurchase.getAccountId());
		addMessage("account_created", account.getClass().getName());
		// Crea la cuenta de Facura
		AccountInvoice accountInvoice = new AccountInvoice();
		accountInvoice.setAccountId(account.getAccountId());
		accountInvoice.setAccount(account);
		accountInvoice.setPerson(accountInvoicePurchase.getPerson());
		accountInvoice.setProduct(account.getProduct());
		accountInvoice.setIssueDate(accountInvoicePurchase.getIssueDate());
		accountInvoice.setRegistrationDate(accountInvoicePurchase.getRegistrationDate());
		InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
		accountInvoice.setInvoiceVoucherType(typeInvoice);
		accountInvoice.setRemark("FACTURA DE LA COMPRA "+accountInvoicePurchase.getAccountId());
		accountInvoice.setUserRegistering(accountInvoicePurchase.getUserRegistering());
		accountInvoice.setEstablishmentCode(accountInvoicePurchase.getEstablishmentCode());
		accountInvoice.setEmissionPointCode(accountInvoicePurchase.getEmissionPointCode());
		accountInvoice.setSequentialCode(accountInvoicePurchase.getSequentialCode());
		accountInvoice.setAuthorizationCode(ParameterHelper.getValue("AUTHORIZATION_CODE"));
		accountInvoice.setUnity(accountInvoicePurchase.getUnity());
		XPersistence.getManager().persist(accountInvoice);
		
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
