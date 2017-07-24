package com.powerfin.actions.accountInvoice;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class ConvertOrderInvoiceToInvoicePurchase extends SaveAction{

	public void execute() throws Exception {
		AccountInvoice accountOrder=XPersistence.getManager().find(AccountInvoice.class, getView().getValueString("accountId"));
		//String transactionModuleId;
		
		String accountStatusId = AccountInvoiceHelper.STATUS_INVOICE_REQUEST;	
		String productId = "202";
		String externalCode = "";
		
		// Crea la cuenta
		Account account = AccountHelper.createAccount(productId, accountOrder.getPerson().getPersonId(), accountStatusId, null, externalCode, accountOrder.getAccountId(), accountOrder.getAccount().getBranch().getBranchId());
		addMessage("account_created", account.getClass().getName());
		// Crea la cuenta de Facura
		AccountInvoice accountInvoice = new AccountInvoice();
		accountInvoice.setAccountId(account.getAccountId());
		accountInvoice.setAccount(account);
		accountInvoice.setPerson(accountOrder.getPerson());
		accountInvoice.setProduct(account.getProduct());
		accountInvoice.setIssueDate(accountOrder.getIssueDate());
		accountInvoice.setRegistrationDate(accountOrder.getRegistrationDate());
		InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
		accountInvoice.setInvoiceVoucherType(typeInvoice);
		accountInvoice.setRemark(XavaResources.getString("invoice_based_on_order", accountOrder.getAccountId()));
		accountInvoice.setUserRegistering(accountOrder.getUserRegistering());
		accountInvoice.setEstablishmentCode(accountOrder.getEstablishmentCode());
		accountInvoice.setEmissionPointCode(accountOrder.getEmissionPointCode());
		accountInvoice.setSequentialCode(accountOrder.getSequentialCode());
		accountInvoice.setAuthorizationCode(ParameterHelper.getValue("AUTHORIZATION_CODE"));
		accountInvoice.setUnity(accountOrder.getUnity());
		XPersistence.getManager().persist(accountInvoice);
		
		// Creacion de los detalles
		List <AccountInvoiceDetail> invoiceDetails=accountOrder.getDetails();
		for(AccountInvoiceDetail accountDetail:invoiceDetails){
			AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
			accountInvoiceDetail.setAccountInvoice(accountInvoice);
			accountInvoiceDetail.setAccountDetail(accountDetail.getAccountDetail());
			accountInvoiceDetail.setTax(accountDetail.getTax());
			accountInvoiceDetail.setQuantity(accountDetail.getQuantity());
			accountInvoiceDetail.setUnitPrice(accountDetail.getUnitPrice());
			accountInvoiceDetail.setDiscount(accountDetail.getDiscount());
			accountInvoiceDetail.setAmount(accountDetail.getAmount());
			accountInvoiceDetail.setTaxAmount(accountDetail.getTaxAmount());
			accountInvoiceDetail.setFinalAmount(accountDetail.getFinalAmount());
			XPersistence.getManager().persist(accountInvoiceDetail);
		}
	}
}