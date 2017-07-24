package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.actions.transaction.*;
import com.powerfin.helper.*;
import com.powerfin.model.types.*;

/**
 * The persistent class for the transaction database table.
 * 
 */
@Entity
@Table(name = "transaction")
@Views({
		@View(members = "voucher;" + "currency;transactionModule;" + "value;"
				+ "remark;" + "requestDate, userRequesting;"
				+ "authorizationDate, documentNumber, userAuthorizing;" 
				+ "accountingDate;"
				+ "transactionStatus;"
				+ "transactionAccounts"),
		
		@View(name="TransactionList", members = "voucher;" + "currency; transactionModule;" + "value;"
				+ "remark;" + "requestDate, userRequesting;"
				+ "authorizationDate, userAuthorizing;" 
				+ "accountingDate, documentNumber;"
				+ "originationBranch, destinationBranch;"
				+ "transactionStatus;"
				+ "transactionAccounts"),
		
		//General transaction
		@View(name="RequestTXGeneral", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus;"
				+ "transactionAccounts;"),
		@View(name="AuthorizeTXGeneral", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus;"
				+ "transactionAccounts;"),
		
		//ManualAccountingEntry
		@View(name="RequestTXManualAccountingEntry", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus;"
				+ "transactionAccounts;"),
		@View(name="AuthorizeTXManualAccountingEntry", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus;"
				+ "transactionAccounts;"),
		//Opening
		@View(name = "RequestTXOpening", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "shareholderAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXOpening", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "shareholderAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Transfer Sent by Bank
		@View(name = "RequestTXTransferSent", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "bankAccount[creditAccount];"
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXTransferSent", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "bankAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Transfer Received by Bank
		@View(name = "RequestTXTransferReceived", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXTransferReceived", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" 
				+ "remark;" 
				+ "transactionStatus;"),
		
		//Debit note Bank
		@View(name = "RequestTXDebitBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[creditAccount];"
				+ "otherAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXDebitBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[creditAccount];"
				+ "otherAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Credit note Bank
		@View(name = "RequestTXCreditBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[debitAccount];"
				+ "otherAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXCreditBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "bankAccount[debitAccount];"
				+ "otherAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Credit note Payable
		@View(name = "RequestTXCreditPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "PayableAccount[creditAccount];"
				+ "concept[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXCreditPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "PayableAccount[creditAccount];"
				+ "concept[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Debit note Payable
		@View(name = "RequestTXDebitPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "PayableAccount[debitAccount];"
				+ "concept[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXDebitPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "PayableAccount[debitAccount];"
				+ "concept[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Transfer Payable
		@View(name = "RequestTXTransferPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXTransferPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Transfer Bank
		@View(name = "RequestTXTransferBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXTransferBank", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Advance (Payable)
		@View(name = "RequestTXAdvance", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Account[creditAccount]; "
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXAdvance", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Account[creditAccount];"
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Advance Payment (Payable)
		@View(name = "RequestTXAdvancePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Account[debitAccount];"
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXAdvancePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Account[debitAccount];" 
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Third Advance Payment (Payable)
		@View(name = "RequestTXThirdAdvancePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "AccountAdvance[creditAccount];"
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXThirdAdvancePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "DebitAccount[debitAccount];"
				+ "AccountAdvance[creditAccount];" 
				+ "AdvanceType[secondaryCategory]; "
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Invoice Sale Payment
		@View(name = "RequestTXInvoiceSalePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[creditAccount];"
				+ "DebitAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXInvoiceSalePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[creditAccount];"
				+ "DebitAccount[debitAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Invoice Purchase Payment
		@View(name = "RequestTXInvoicePurchasePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[debitAccount];"
				+ "CashAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXInvoicePurchasePayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[debitAccount];"
				+ "CashAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),

		//Invoice Purchase Check Payment
		@View(name = "RequestTXInvoicePurchaseCheckPayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[debitAccount];"
				+ "bankAccount[creditAccount];" 
				+ "documentNumber; value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXInvoicePurchaseCheckPayment", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "Invoice[debitAccount];"
				+ "bankAccount[creditAccount];" 
				+ "documentNumber; value;" + "remark;" + "transactionStatus"),

		//Check Payment Payable
		@View(name = "RequestTXCheckPaymentPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "bankAccount[creditAccount];"
				+ "documentNumber; value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXCheckPaymentPayable", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "bankAccount[creditAccount];" 
				+ "documentNumber; value;" + "remark;" + "transactionStatus"),
		
		//Authorize Invoice Purchase
		@View(name = "AuthorizeTXInvoicePurchase", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "invoice[creditAccount];" 
				+ "data[accountInvoicePurchase];"),
		
		//Authorize Invoice Sale
		@View(name = "AuthorizeTXInvoiceSale", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "invoice[debitAccount];" 
				+ "data[accountInvoiceSale];"),
		
		//Authorize Credit Note Purchase
		@View(name = "AuthorizeTXCreditNotePurchase", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "creditNote[debitAccount];" 
				+ "data[accountInvoiceSale];"),

		//Authorize Credit Note Sale
		@View(name = "AuthorizeTXCreditNoteSale", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "creditNote[creditAccount];" 
				+ "data[accountInvoicePurchase];"),
				
		//Authorize Retention on Invoice Purchase
		@View(name = "AuthorizeTXRetentionPurchase", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "retention[creditAccount];" 
				+ "data[accountRetentionPurchase];"),
		
		//Authorize Retention on Invoice Sale
		@View(name = "AuthorizeTXRetentionSale", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "retention[debitAccount];" 
				+ "data[accountRetentionSale];"),
		
		//Authorize Account Loan
		@View(name = "AuthorizeTXAccountLoan", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "loan[debitAccount];" 
				+ "data[accountLoan];"),
		
		//Authorize Account term
		@View(name = "AuthorizeTXAccountTerm", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "term[creditAccount];" 
				+ "data[accountTerm];"),
		
		//Authorize Purchase Portfolio
		@View(name = "AuthorizeTXPurchasePortfolio", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "loan[debitAccount];" 
				+ "data[accountLoan];"),
		
		//Authorize Sale Portfolio
		@View(name = "AuthorizeTXSalePortfolio", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "value; transactionStatus;"
				+ "loan[debitAccount];" 
				+ "data[accountLoan];"),
		
		//Transfer Due, Invoice purchase
		@View(name = "RequestTXTransferDue", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "invoice[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXTransferDue", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "invoice[debitAccount];"
				+ "CreditAccount[creditAccount];" 
				+ "value;" + "remark;" + "transactionStatus"),
		
		//Purchase Foreign Exchange Customer
		@View(name = "RequestTXPurchaseForexCustomer", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXPurchaseForexCustomer", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Sale Foreign Exchange Customer
		@View(name = "RequestTXSaleForexCustomer", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXSaleForexCustomer", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Purchase Foreign Exchange Bank
		@View(name = "RequestTXPurchaseForexBank", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXPurchaseForexBank", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Sale Foreign Exchange Bank
		@View(name = "RequestTXSaleForexBank", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXSaleForexBank", members = "#currency;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "debitAccount[debitAccount];"
				+ "creditAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//PurchasePortfolio Payment
		@View(name = "RequestTXPurchasePortfolioPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXPurchasePortfolioPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//PurchasePortfolio Payment Value Date
		@View(name = "RequestTXPurchasePortfolioPaymentValueDate", members = "#currency; transactionModule, voucher; valueDate; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXPurchasePortfolioPaymentValueDate", members = "#currency; transactionModule, voucher; valueDate; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Sale Portfolio Payment
		@View(name = "RequestTXSalePortfolioPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[debitAccount];"
				+ "brokerAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXSalePortfolioPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[debitAccount];"
				+ "brokerAccount[creditAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Loan Payment
		@View(name = "RequestTXLoanPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		@View(name = "AuthorizeTXLoanPayment", members = "#currency; transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "loanAccount[creditAccount];"
				+ "debitAccount[debitAccount];" 
				+ "value;" + "exchangeRate;" + "remark;" + "transactionStatus"),
		
		//Transfer Items between Branch
		@View(name="RequestTXTransferItem", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus; "
				+ "originationBranch, destinationBranch; "
				+ "transactionAccounts;"),
		@View(name="AuthorizeTXTransferItem", members = "#currency, exchangeRate;transactionModule, voucher; accountingDate, companyAccountingDate;"
				+ "remark;" + "transactionStatus;"
				+ "originationBranch, destinationBranch; "
				+ "transactionAccounts;"),
		
})
@Tabs({
		@Tab(properties = "voucher, currency.currencyId, transactionModule.transactionModuleId, value, transactionStatus.name, requestDate, authorizationDate, accountingDate"),
		@Tab(name = "TransactionList", properties = "voucher, currency.currencyId, transactionModule.transactionModuleId, transactionModule.name, transactionStatus.name, accountingDate, debitAccount.accountId, creditAccount.accountId"),
		@Tab(name = "TXGeneral", properties = "voucher, currency.currencyId, remark, transactionModule.name, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001'"),
		@Tab(name = "TXOpening", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'OPENING'"),
		@Tab(name = "TXTransferSent", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERSENT'"),
		@Tab(name = "TXTransferReceived", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERRECEIVED'"),
		@Tab(name = "TXDebitBank", properties = "creditAccount.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'DEBITBANK'"),
		@Tab(name = "TXCreditBank", properties = "debitAccount.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'CREDITBANK'"),
		@Tab(name = "TXCreditPayable", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'CREDITPAYABLE'"),
		@Tab(name = "TXDebitPayable", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'DEBITPAYABLE'"),
		@Tab(name = "TXTransferPayable", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERPAYABLE'"),
		@Tab(name = "TXTransferBank", properties = "debitAccount.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERBANK'"),
		@Tab(name = "TXAdvance", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'ADVANCE'"),
		@Tab(name = "TXAdvancePayment", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'ADVANCEPAYMENT'"),
		@Tab(name = "TXThirdAdvancePayment", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'THIRDADVANCEPAYMENT'"),
		@Tab(name = "TXManualAccountingEntry", properties = "voucher, currency.currencyId, remark, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'MANUALACCOUNTINGENTRY'"),
		@Tab(name = "TXInvoiceSalePayment", properties = "creditAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'INVOICESALEPAYMENT'"),
		@Tab(name = "TXInvoicePurchasePayment", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'INVOICEPURCHASEPAYMENT'"),
		@Tab(name = "TXInvoicePurchaseCheckPayment", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'INVOICEPURCHASECHECKPAYMENT'"),
		@Tab(name = "TXCheckPaymentPayable", properties = "debitAccount.person.name, voucher, currency.currencyId, remark, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'CHECKPAYMENTPAYABLE'"),
		@Tab(name = "TXInvoicePurchase", properties = "creditAccount.person.name, voucher, creditAccount.code, creditAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'INVOICE_PURCHASE'"),
		@Tab(name = "TXInvoiceSale", properties = "debitAccount.person.name, voucher, debitAccount.code, debitAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'INVOICE_SALE'"),
		@Tab(name = "TXCreditNotePurchase", properties = "debitAccount.person.name, voucher, debitAccount.code, debitAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'CREDITNOTEPURCHASE'"),
		@Tab(name = "TXCreditNoteSale", properties = "creditAccount.person.name, voucher, creditAccount.code, creditAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'CREDITNOTESALE'"),
		@Tab(name = "TXRetentionSale", properties = "debitAccount.person.name, voucher, debitAccount.code, debitAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'RETENTION_SALE'"),
		@Tab(name = "TXRetentionPurchase", properties = "creditAccount.person.name, voucher, creditAccount.code, creditAccount.branch.name, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'RETENTION_PURCHASE'"),
		@Tab(name = "TXAccountLoan", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'LOANDISBURSEMENT'"),
		@Tab(name = "TXAccountTerm", properties = "creditAccount.person.name, voucher, creditAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TERMOPENING'"),
		@Tab(name = "TXTransferDue", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERDUE'"),
		@Tab(name = "TXPurchaseForexCustomer", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'PURCHASEFOREXCUSTOMER'"),
		@Tab(name = "TXSaleForexCustomer", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'SALEFOREXCUSTOMER'"),
		@Tab(name = "TXPurchaseForexBank", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'PURCHASEFOREXBANK'"),
		@Tab(name = "TXSaleForexBank", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'SALEFOREXBANK'"),
		@Tab(name = "TXInBatch", properties = "transactionModule.name, voucher, debitAccount.accountId, creditAccount.accountId, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001'"),
		@Tab(name = "TXPurchasePortfolio", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'PURCHASEPORTFOLIO'"),
		@Tab(name = "TXSalePortfolio", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'SALEPORTFOLIO'"),
		@Tab(name = "TXPurchasePortfolioPayment", properties = "creditAccount.person.name, voucher, creditAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'PURCHASEPORTFOLIOPAYMENT'"),
		@Tab(name = "TXPurchasePortfolioPaymentValueDate", properties = "creditAccount.person.name, voucher, creditAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'PURCHASEPORTFOLIOPAYMENTVD'"),
		@Tab(name = "TXSalePortfolioPayment", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'SALEPORTFOLIOPAYMENT'"),
		@Tab(name = "TXLoanPayment", properties = "debitAccount.person.name, voucher, debitAccount.code, currency.currencyId, value, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'LOANPAYMENT'"),
		@Tab(name = "TXTransferItem", properties = "voucher, currency.currencyId, remark, transactionStatus.name, accountingDate", baseCondition = "${transactionStatus.transactionStatusId} = '001' and ${transactionModule.transactionModuleId} = 'TRANSFERITEM'"),
})
public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "transaction_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String transactionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "authorization_date")
	@ReadOnly
	private Date authorizationDate;

	@Column(name = "exchange_rate", precision = 10, scale = 7)
	@ReadOnly(notForViews ="RequestTXPurchaseForexCustomer,"
			+ "RequestTXSaleForexCustomer,"
			+ "RequestTXPurchaseForexBank,"
			+ "RequestTXSaleForexBank,"
			)
	private BigDecimal exchangeRate;

	@Column(length = 4000)
	@Required
	@ReadOnly(forViews = ""
			+ "AuthorizeTXGeneral,"
			+ "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXManualAccountingEntry,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio,"
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue, "
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXLoanPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	private String remark;

	@Column(length = 50)
	@DisplaySize(30)
	@ReadOnly
	private String voucher;

	@Column(name="document_number", length = 50)
	@DisplaySize(20)
	@ReadOnly(forViews = ""
			+ "AuthorizeTXGeneral,"
			+ "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXManualAccountingEntry,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio,"
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue,"
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXLoanPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	private String documentNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "request_date")
	@ReadOnly
	private Date requestDate;

	@Column(name = "user_authorizing", length = 30)
	@ReadOnly
	private String userAuthorizing;

	@Column(name = "user_requesting", length = 30)
	@ReadOnly
	private String userRequesting;

	@Column(nullable = false, precision = 19, scale = 2)
	//@Required
	@ReadOnly(forViews = ""
			+ "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio, "
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue, "
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXLoanPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	private BigDecimal value;

	@ManyToOne
	@JoinColumn(name="currency_id", nullable=true)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsLists({
		@DescriptionsList(descriptionProperties="currencyId, name"),
		@DescriptionsList(forViews="RequestTXPurchaseForexCustomer,"
				+ "RequestTXSaleForexCustomer,"
				+ "RequestTXPurchaseForexBank,"
				+ "RequestTXSaleForexBank", 
						descriptionProperties="currencyId, name",
						condition="${currencyId} not in (select c.officialCurrency from Company c where c.companyId=1)")
	})
	
	@OnChange(OnModifyTransactionCurrency.class)
	@ReadOnly(forViews = ""
			+ "AuthorizeTXGeneral,"
			+ "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXManualAccountingEntry,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio, "
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue, "
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXLoanPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	private Currency currency;
	
	// bi-directional many-to-one association to TransactionModule
	@ManyToOne
	@JoinColumn(name = "transaction_module_id", nullable = false)
	@NoCreate
	@NoModify
	@NoSearch
	@Required
	@NoFrame
	@ReferenceViews({
		@ReferenceView(value = "simple", 
				forViews = "TransactionList"),
	@ReferenceView(value = "forTransaction", 
			forViews = ""
					+ "AuthorizeTXGeneral,"
					+ "AuthorizeTXOpening, "
					+ "AuthorizeTXTransferSent, "
					+ "AuthorizeTXTransferReceived,"
					+ "AuthorizeTXDebitBank,"
					+ "AuthorizeTXCreditBank,"
					+ "AuthorizeTXCreditPayable,"
					+ "AuthorizeTXDebitPayable,"
					+ "AuthorizeTXTransferPayable,"
					+ "AuthorizeTXTransferBank,"
					+ "AuthorizeTXAdvance,"
					+ "AuthorizeTXAdvancePayment,"
					+ "AuthorizeTXThirdAdvancePayment,"
					+ "AuthorizeTXManualAccountingEntry,"
					+ "AuthorizeTXInvoiceSalePayment,"
					+ "AuthorizeTXInvoicePurchasePayment,"
					+ "AuthorizeTXInvoicePurchaseCheckPayment,"
					+ "AuthorizeTXCheckPaymentPayable,"
					+ "AuthorizeTXInvoicePurchase,"
					+ "AuthorizeTXInvoiceSale,"
					+ "AuthorizeTXRetentionPurchase,"
					+ "AuthorizeTXRetentionSale,"
					+ "AuthorizeTXAccountLoan,"
					+ "AuthorizeTXAccountTerm,"
					+ "AuthorizeTXPurchasePortfolio, "
					+ "AuthorizeTXSalePortfolio,"
					+ "AuthorizeTXTransferDue, "
					+ "AuthorizeTXPurchaseForexCustomer,"
					+ "AuthorizeTXSaleForexCustomer,"
					+ "AuthorizeTXPurchaseForexBank,"
					+ "AuthorizeTXSaleForexBank,"
					+ "AuthorizeTXPurchasePortfolioPayment,"
					+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
					+ "AuthorizeTXSalePortfolioPayment,"
					+ "AuthorizeTXLoanPayment,"
					+ "AuthorizeTXCreditNotePurchase,"
					+ "AuthorizeTXCreditNoteSale,"
					+ "AuthorizeTXTransferItem,"
					)
	})
	@ReadOnly(forViews = ""
			+ "AuthorizeTXGeneral,"
			+ "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXManualAccountingEntry,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio, "
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue, "
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXLoanPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	
	@DescriptionsLists({ 
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXOpening", condition = "${transactionModuleId} = 'OPENING'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferSent", condition = "${transactionModuleId} = 'TRANSFERSENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferReceived", condition = "${transactionModuleId} = 'TRANSFERRECEIVED'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXDebitBank", condition = "${transactionModuleId} = 'DEBITBANK'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXCreditBank", condition = "${transactionModuleId} = 'CREDITBANK'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXCreditPayable", condition = "${transactionModuleId} = 'CREDITPAYABLE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXDebitPayable", condition = "${transactionModuleId} = 'DEBITPAYABLE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferPayable", condition = "${transactionModuleId} = 'TRANSFERPAYABLE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferBank", condition = "${transactionModuleId} = 'TRANSFERBANK'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXAdvance", condition = "${transactionModuleId} = 'ADVANCE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXAdvancePayment", condition = "${transactionModuleId} = 'ADVANCEPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXThirdAdvancePayment", condition = "${transactionModuleId} = 'THIRDADVANCEPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXManualAccountingEntry", condition = "${transactionModuleId} = 'MANUALACCOUNTINGENTRY'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXGeneral", condition = ""),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXInvoiceSalePayment", condition = "${transactionModuleId} = 'INVOICESALEPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXInvoicePurchasePayment", condition = "${transactionModuleId} = 'INVOICEPURCHASEPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXInvoicePurchaseCheckPayment", condition = "${transactionModuleId} = 'INVOICEPURCHASECHECKPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXCheckPaymentPayable", condition = "${transactionModuleId} = 'CHECKPAYMENTPAYABLE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferDue", condition = "${transactionModuleId} = 'TRANSFERDUE'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXPurchaseForexCustomer", condition = "${transactionModuleId} = 'PURCHASEFOREXCUSTOMER'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXSaleForexCustomer", condition = "${transactionModuleId} = 'SALEFOREXCUSTOMER'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXPurchaseForexBank", condition = "${transactionModuleId} = 'PURCHASEFOREXBANK'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXSaleForexBank", condition = "${transactionModuleId} = 'SALEFOREXBANK'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXPurchasePortfolioPayment", condition = "${transactionModuleId} = 'PURCHASEPORTFOLIOPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXPurchasePortfolioPaymentValueDate", condition = "${transactionModuleId} = 'PURCHASEPORTFOLIOPAYMENTVD'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXSalePortfolioPayment", condition = "${transactionModuleId} = 'SALEPORTFOLIOPAYMENT'"),
		@DescriptionsList(descriptionProperties = "name", forViews = "RequestTXTransferItem", condition = "${transactionModuleId} = 'TRANSFERITEM'"),
		
	})
	private TransactionModule transactionModule;

	// bi-directional many-to-one association to TransactionStatus
	@ManyToOne
	@JoinColumn(name = "transaction_status_id", nullable = false)
	@NoCreate
	@NoModify
	@Required
	@DescriptionsLists({
			@DescriptionsList(descriptionProperties = "name", condition = "${transactionStatusId} IN ('001')",
					forViews = "RequestTXGeneral, "
							+ "RequestTXOpening, "
							+ "RequestTXTransferSent, "
							+ "RequestTXTransferReceived,"
							+ "RequestTXDebitBank,"
							+ "RequestTXCreditBank,"
							+ "RequestTXCreditPayable,"
							+ "RequestTXDebitPayable,"
							+ "RequestTXTransferPayable,"
							+ "RequestTXTransferBank,"
							+ "RequestTXAdvance,"
							+ "RequestTXAdvancePayment,"
							+ "RequestTXThirdAdvancePayment,"
							+ "RequestTXManualAccountingEntry,"
							+ "RequestTXGeneral,"
							+ "RequestTXInvoiceSalePayment, "
							+ "RequestTXInvoicePurchasePayment, "
							+ "RequestTXInvoicePurchaseCheckPayment, "
							+ "RequestTXCheckPaymentPayable, "
							+ "RequestTXTransferDue, "
							+ "RequestTXPurchaseForexCustomer,"
							+ "RequestTXSaleForexCustomer,"
							+ "RequestTXPurchaseForexBank,"
							+ "RequestTXSaleForexBank,"
							+ "RequestTXPurchasePortfolioPayment,"
							+ "RequestTXPurchasePortfolioPaymentValueDate,"
							+ "RequestTXSalePortfolioPayment,"
							+ "RequestTXTransferItem,"
							),
			@DescriptionsList(descriptionProperties = "name", condition = "${transactionStatusId} IN ('001','002','003')", 
					forViews = "AuthorizeTXGeneral, "
							+ "AuthorizeTXOpening,"
							+ "AuthorizeTXTransferSent, "
							+ "AuthorizeTXTransferReceived,"
							+ "AuthorizeTXDebitBank,"
							+ "AuthorizeTXCreditBank,"
							+ "AuthorizeTXCreditPayable,"
							+ "AuthorizeTXDebitPayable,"
							+ "AuthorizeTXTransferPayable,"
							+ "AuthorizeTXTransferBank,"
							+ "AuthorizeTXAdvance,"
							+ "AuthorizeTXAdvancePayment,"
							+ "AuthorizeTXThirdAdvancePayment,"
							+ "AuthorizeTXManualAccountingEntry,"
							+ "AuthorizeTXInvoiceSalePayment,"
							+ "AuthorizeTXInvoicePurchasePayment,"
							+ "AuthorizeTXInvoicePurchaseCheckPayment,"
							+ "AuthorizeTXCheckPaymentPayable,"
							+ "AuthorizeTXInvoicePurchase,"
							+ "AuthorizeTXInvoiceSale,"
							+ "AuthorizeTXRetentionPurchase,"
							+ "AuthorizeTXRetentionSale,"
							+ "AuthorizeTXAccountLoan,"
							+ "AuthorizeTXAccountTerm,"
							+ "AuthorizeTXPurchasePortfolio, "
							+ "AuthorizeTXSalePortfolio,"
							+ "AuthorizeTXTransferDue, "
							+ "AuthorizeTXPurchaseForexCustomer,"
							+ "AuthorizeTXSaleForexCustomer,"
							+ "AuthorizeTXPurchaseForexBank,"
							+ "AuthorizeTXSaleForexBank,"
							+ "AuthorizeTXPurchasePortfolioPayment,"
							+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
							+ "AuthorizeTXSalePortfolioPayment,"
							+ "AuthorizeTXLoanPayment,"
							+ "AuthorizeTXCreditNotePurchase,"
							+ "AuthorizeTXCreditNoteSale,"
							+ "AuthorizeTXTransferItem,"
							) 
			})
	@ReferenceView(forViews = "DEFAULT, TransactionList", value = "simple")
	@NoFrame(forViews = "DEFAULT, TransactionList")
	private TransactionStatus transactionStatus;

	// bi-directional many-to-one association to TransactionAccount
	@OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListsProperties({
		@ListProperties("account.accountId, account.code, account.name, subaccount, category.categoryId, debitOrCredit, value"),
		@ListProperties(forViews="TransactionList", value="account.accountId, account.code, account.name, subaccount, category.categoryId, debitOrCredit, value"),
		@ListProperties(forViews="AuthorizeTXManualAccountingEntry,RequestTXManualAccountingEntry",
					value="account.code, account.name, subaccount, category.categoryId, debitOrCredit, value"),
		@ListProperties(forViews="AuthorizeTXGeneral,RequestTXGeneral",
					value="account.accountId, account.code, account.name, subaccount, category.categoryId, debitOrCredit, value"),
		@ListProperties(forViews="AuthorizeTXTransferItem,RequestTXTransferItem",
					value="debitOrCredit, branch.name, account.accountId, account.code, account.alternateCode, account.name, quantity, value"),
	})
	@ReadOnly(forViews = "AuthorizeTXGeneral, AuthorizeTXManualAccountingEntry, AuthorizeTXTransferItem")
	@CollectionViews({
		@CollectionView(forViews="TransactionList",value="ForList"),
		@CollectionView(forViews="AuthorizeTXManualAccountingEntry,RequestTXManualAccountingEntry",value="ForManualEntry"),
		@CollectionView(forViews="AuthorizeTXTransferItem,RequestTXTransferItem",value="ForTransferItem"),
		@CollectionView(forViews="AuthorizeTXGeneral,RequestTXGeneral",value="ForGeneral")
		
	})
	
	@ListActions({
		@ListAction("Print.generatePdf"),
		@ListAction("Print.generateExcel")
	})
	private List<TransactionAccount> transactionAccounts;

	// bi-directional one-to-one association to TranOpening
	@OneToOne(mappedBy = "transaction")
	private Financial financial;

	@ManyToOne
	@JoinColumn(name = "debit_account_id")
	//@Required
	@NoCreate
	@NoModify
	@ReferenceViews({
		@ReferenceView("simple"),
		@ReferenceView(forViews="RequestTXTransferDue, "
				+ "RequestTXInvoiceSalePayment, "
				+ "RequestTXInvoicePurchasePayment, "
				+ "RequestTXInvoicePurchaseCheckPayment, "
				+ "RequestTXPurchasePortfolioPayment, "
				+ "RequestTXPurchasePortfolioPaymentValueDate, "
				+ "AuthorizeTXTransferDue, "
				+ "AuthorizeTXInvoiceSalePayment, "
				+ "AuthorizeTXInvoicePurchaseCheckPayment, "
				+ "AuthorizeTXInvoicePurchasePayment, "
				+ "AuthorizeTXPurchasePortfolioPayment, "
				+ "AuthorizeTXPurchasePortfolioPaymentValueDate,",
				value="simpleBalance"),
		@ReferenceView(forViews="RequestTXSalePortfolioPayment, AuthorizeTXSalePortfolioPayment", value="SalePortfolioPayment")
	})
	@NoFrame
	@SearchActions({
		@SearchAction(forViews="RequestTXOpening", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferSent", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferReceived", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXDebitBank", value="SearchAccount.SearchAccountToDebitBank"),
		@SearchAction(forViews="RequestTXCreditBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXCreditPayable", value="SearchAccount.SearchAccountToCreditPayable"),
		@SearchAction(forViews="RequestTXDebitPayable", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferPayable", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXAdvance", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXAdvancePayment", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXThirdAdvancePayment", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXInvoiceSalePayment", value="SearchAccount.SearchPayableAndCashAccount"),
		@SearchAction(forViews="RequestTXInvoicePurchasePayment", value="SearchAccount.SearchInvoicePurchaseToPayment"),
		@SearchAction(forViews="RequestTXInvoicePurchaseCheckPayment", value="SearchAccount.SearchInvoicePurchaseToPayment"),
		@SearchAction(forViews="RequestTXCheckPaymentPayable", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferDue", value="SearchAccount.SearchInvoicePurchaseToPayment"),
		@SearchAction(forViews="RequestTXPurchaseForexCustomer", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXSaleForexCustomer", value="SearchAccount.SearchOfficialPayableAccount"),
		@SearchAction(forViews="RequestTXPurchaseForexBank", value="SearchAccount.SearchOfficialBankAccount"),
		@SearchAction(forViews="RequestTXSaleForexBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXPurchasePortfolioPayment", value="SearchAccount.SearchPayableAndCashAccount"),
		@SearchAction(forViews="RequestTXPurchasePortfolioPaymentValueDate", value="SearchAccount.SearchPayableAndCashAccount"),
		@SearchAction(forViews="RequestTXSalePortfolioPayment", value="SearchAccount.SearchSalePortfolioForPayment"),
		
	})
	@ReadOnly(forViews = "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio, "
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue, "
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	@Action(forViews="RequestTXSalePortfolioPayment", value = "AccountLoan.GetOverdueBalanceSP", alwaysEnabled=true )
	private Account debitAccount;

	@ManyToOne
	@JoinColumn(name = "credit_account_id")
	//@Required
	@NoCreate
	@NoModify
	@ReferenceViews({
		@ReferenceView("simple"),
		@ReferenceView(forViews="RequestTXInvoiceSalePayment, AuthorizeTXInvoiceSalePayment",value="simpleBalance"),
		@ReferenceView(forViews="RequestTXPurchasePortfolioPayment, RequestTXPurchasePortfolioPaymentValueDate, "
				+ "AuthorizeTXPurchasePortfolioPayment, AuthorizeTXPurchasePortfolioPaymentValueDate", value="PurchasePortfolioPayment")
	})
	@NoFrame
	@ReadOnly(forViews = "AuthorizeTXOpening, "
			+ "AuthorizeTXTransferSent, "
			+ "AuthorizeTXTransferReceived,"
			+ "AuthorizeTXDebitBank,"
			+ "AuthorizeTXCreditBank,"
			+ "AuthorizeTXCreditPayable,"
			+ "AuthorizeTXDebitPayable,"
			+ "AuthorizeTXTransferPayable,"
			+ "AuthorizeTXTransferBank,"
			+ "AuthorizeTXAdvance,"
			+ "AuthorizeTXAdvancePayment,"
			+ "AuthorizeTXThirdAdvancePayment,"
			+ "AuthorizeTXInvoiceSalePayment,"
			+ "AuthorizeTXInvoicePurchasePayment,"
			+ "AuthorizeTXInvoicePurchaseCheckPayment,"
			+ "AuthorizeTXCheckPaymentPayable,"
			+ "AuthorizeTXInvoicePurchase,"
			+ "AuthorizeTXInvoiceSale,"
			+ "AuthorizeTXRetentionPurchase,"
			+ "AuthorizeTXRetentionSale,"
			+ "AuthorizeTXAccountLoan,"
			+ "AuthorizeTXAccountTerm,"
			+ "AuthorizeTXPurchasePortfolio, "
			+ "AuthorizeTXSalePortfolio,"
			+ "AuthorizeTXTransferDue,"
			+ "AuthorizeTXPurchaseForexCustomer,"
			+ "AuthorizeTXSaleForexCustomer,"
			+ "AuthorizeTXPurchaseForexBank,"
			+ "AuthorizeTXSaleForexBank,"
			+ "AuthorizeTXPurchasePortfolioPayment,"
			+ "AuthorizeTXPurchasePortfolioPaymentValueDate,"
			+ "AuthorizeTXSalePortfolioPayment,"
			+ "AuthorizeTXCreditNotePurchase,"
			+ "AuthorizeTXCreditNoteSale,"
			+ "AuthorizeTXTransferItem,"
			)
	@SearchActions({ 
		@SearchAction(forViews="RequestTXOpening", value = "SearchAccount.SearchShareholderAccount"),
		@SearchAction(forViews="RequestTXTransferSent", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXTransferReceived", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXDebitBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXCreditBank", value="SearchAccount.SearchAccountToCreditBank"),
		@SearchAction(forViews="RequestTXCreditPayable", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXDebitPayable", value="SearchAccount.SearchAccountToDebitPayable"),
		@SearchAction(forViews="RequestTXTransferPayable", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXTransferBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXAdvance", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXAdvancePayment", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXThirdAdvancePayment", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXInvoiceSalePayment", value="SearchAccount.SearchInvoiceSaleToPayment"),
		@SearchAction(forViews="RequestTXInvoicePurchasePayment", value="SearchAccount.SearchCashAndBankAccount"),
		@SearchAction(forViews="RequestTXInvoicePurchaseCheckPayment", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXCheckPaymentPayable", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXTransferDue", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXPurchaseForexCustomer", value="SearchAccount.SearchOfficialPayableAccount"),
		@SearchAction(forViews="RequestTXSaleForexCustomer", value="SearchAccount.SearchPayableAccount"),
		@SearchAction(forViews="RequestTXPurchaseForexBank", value="SearchAccount.SearchBankAccount"),
		@SearchAction(forViews="RequestTXSaleForexBank", value="SearchAccount.SearchOfficialBankAccount"),
		@SearchAction(forViews="RequestTXPurchasePortfolioPayment", value="SearchAccount.SearchPurchasePortfolioForPayment"),
		@SearchAction(forViews="RequestTXPurchasePortfolioPaymentValueDate", value="SearchAccount.SearchPurchasePortfolioForPayment"),
		@SearchAction(forViews="RequestTXSalePortfolioPayment", value="SearchAccount.SearchAccountSaleNegotiation"),
	})
	@Actions({
		@Action(forViews="RequestTXPurchasePortfolioPayment", value = "AccountLoan.GetOverdueBalancePP", alwaysEnabled=true ),
		@Action(forViews="RequestTXPurchasePortfolioPaymentValueDate", value = "AccountLoan.GetOverdueBalancePPValueDate", alwaysEnabled=true ),
	})
	private Account creditAccount;

	@Version
	private Integer version;

	@Column(name="accounting_date", nullable=false)
	@Temporal(TemporalType.DATE)
	@ReadOnly(notForViews= "DEFAULT")
	private Date accountingDate;
	
	@Column(name="value_date", nullable=false)
	@Temporal(TemporalType.DATE)
	@ReadOnly(notForViews= "DEFAULT, " 
		+"RequestTXPurchasePortfolioPaymentValueDate,"
	)
	private Date valueDate;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="secondary_category_id", nullable=true)
	@DescriptionsLists({
		@DescriptionsList(forViews="RequestTXAdvance, AuthorizeTXAdvance, "
				+ "RequestTXAdvancePayment, AuthorizeTXAdvancePayment, "
				+ "RequestTXThirdAdvancePayment, AuthorizeTXThirdAdvancePayment", 
				condition="${categoryId} in ('ADVANCE','ADVSALPORT')")
	})
	@ReadOnly(forViews = "AuthorizeTXAdvance, AuthorizeTXAdvancePayment, AuthorizeTXThirdAdvancePayment")
	@NoCreate
	@NoModify
	private Category secondaryCategory;
	
	@ManyToOne
	@JoinColumn(name="origination_branch_id")
	@ReadOnly(forViews="AuthorizeTXTransferItem")
	@DescriptionsList(descriptionProperties = "name")
	@NoCreate
	@NoModify
	private Branch originationBranch;
	
	@ManyToOne
	@JoinColumn(name="destination_branch_id")
	@ReadOnly(forViews="AuthorizeTXTransferItem")
	@DescriptionsList(descriptionProperties = "name")
	@NoCreate
	@NoModify
	private Branch destinationBranch;
	
	@ManyToOne
	@JoinColumn(name="origen_unity_id")
	@ReadOnly(forViews="AuthorizeTXTransferItem, AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@DescriptionsList(descriptionProperties = "name")
	@NoCreate
	@NoModify
	private Unity origenUnity;
	
	//-------------------------
	
	@Transient
	@Temporal(TemporalType.DATE)
	@DefaultValueCalculator(com.powerfin.calculators.CurrentAccountingDateCalculator.class)
	@ReadOnly
	private Date companyAccountingDate;
		
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXInvoicePurchase")
	@NoFrame
	@ReadOnly
	private AccountInvoice accountInvoicePurchase;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXInvoiceSale")
	@NoFrame
	@ReadOnly
	private AccountInvoice accountInvoiceSale;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXRetentionSale")
	@NoFrame
	@ReadOnly
	private AccountRetention accountRetentionSale;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXRetentionPurchase")
	@NoFrame
	@ReadOnly
	private AccountRetention accountRetentionPurchase;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXAccountLoan")
	@NoFrame
	@ReadOnly
	private AccountLoan accountLoan;

	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("AuthorizeTXAccountTerm")
	@NoFrame
	@ReadOnly
	private AccountTerm accountTerm;
	
	public Transaction() {
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Date getAuthorizationDate() {
		return this.authorizationDate;
	}

	public void setAuthorizationDate(Date authorizationDate) {
		this.authorizationDate = authorizationDate;
	}

	public BigDecimal getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getUserAuthorizing() {
		return this.userAuthorizing;
	}

	public void setUserAuthorizing(String userAuthorizing) {
		this.userAuthorizing = userAuthorizing;
	}

	public String getUserRequesting() {
		return this.userRequesting;
	}

	public void setUserRequesting(String userRequesting) {
		this.userRequesting = userRequesting;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Financial getFinancial() {
		return this.financial;
	}

	public void setFinancial(Financial financial) {
		this.financial = financial;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	public TransactionStatus getTransactionStatus() {
		return this.transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public List<TransactionAccount> getTransactionAccounts() {
		return this.transactionAccounts;
	}

	public void setTransactionAccounts(
			List<TransactionAccount> transactionAccounts) {
		this.transactionAccounts = transactionAccounts;
	}

	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	public Account getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(Account debitAccount) {
		this.debitAccount = debitAccount;
	}

	public Account getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(Account creditAccount) {
		this.creditAccount = creditAccount;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getAccountingDate() {
		return this.accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Date getCompanyAccountingDate() {
		return CompanyHelper.getCurrentAccountingDate();
	}

	public void setCompanyAccountingDate(Date companyAccountingDate) {
		this.companyAccountingDate = companyAccountingDate;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public AccountInvoice getAccountInvoicePurchase() {
		return XPersistence.getManager().find(AccountInvoice.class, creditAccount.getAccountId());
	}

	public void setAccountInvoicePurchase(AccountInvoice accountInvoicePurchase) {
		this.accountInvoicePurchase = accountInvoicePurchase;
	}

	public AccountInvoice getAccountInvoiceSale() {
		return XPersistence.getManager().find(AccountInvoice.class, debitAccount.getAccountId());
	}

	public void setAccountInvoiceSale(AccountInvoice accountInvoiceSale) {
		this.accountInvoiceSale = accountInvoiceSale;
	}

	public AccountRetention getAccountRetentionSale() {
		return XPersistence.getManager().find(AccountRetention.class, debitAccount.getAccountId());
	}

	public void setAccountRetentionSale(AccountRetention accountRetentionSale) {
		this.accountRetentionSale = accountRetentionSale;
	}

	public AccountRetention getAccountRetentionPurchase() {
		return XPersistence.getManager().find(AccountRetention.class, creditAccount.getAccountId());
	}

	public void setAccountRetentionPurchase(AccountRetention accountRetentionPurchase) {
		this.accountRetentionPurchase = accountRetentionPurchase;
	}

	public AccountLoan getAccountLoan() {
		return XPersistence.getManager().find(AccountLoan.class, debitAccount.getAccountId());
	}

	public void setAccountLoan(AccountLoan accountLoan) {
		this.accountLoan = accountLoan;
	}

	public Category getSecondaryCategory() {
		return secondaryCategory;
	}

	public void setSecondaryCategory(Category secondaryCategory) {
		this.secondaryCategory = secondaryCategory;
	}
	
	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public AccountTerm getAccountTerm() {
		return XPersistence.getManager().find(AccountTerm.class, creditAccount.getAccountId());
	}

	public void setAccountTerm(AccountTerm accountTerm) {
		this.accountTerm = accountTerm;
	}

	@PreCreate
	public void onCreate() throws Exception {
		accountingDate = CompanyHelper.getCurrentAccountingDate();
		voucher = TransactionHelper.getNewVoucher(transactionModule);
		if (value==null)
			value = BigDecimal.ZERO;
		if (exchangeRate==null)
			exchangeRate = ExchangeRateHelper.getExchangeRate(currency);
		fillRequestingInfo();
		fillAuthorizingInfo();
	}

	@PreUpdate
	public void onUpdate() throws Exception {
		if (value==null)
			value = BigDecimal.ZERO;
		if (exchangeRate==null)
			exchangeRate = ExchangeRateHelper.getExchangeRate(currency);
		fillRequestingInfo();
		fillAuthorizingInfo();
	}

	public void fillRequestingInfo() throws Exception {
		if (!transactionStatus.equals(transactionModule.getFinancialTransactionStatus()))
		{
			requestDate = new Date();
			userRequesting = Users.getCurrent();
		}
		if (requestDate==null)
			requestDate = new Date();
		if (userRequesting==null || userRequesting.isEmpty())
			userRequesting = Users.getCurrent();
			
	}

	public void fillAuthorizingInfo() throws Exception {
		if (transactionStatus.equals(transactionModule.getFinancialTransactionStatus()))
		{
			authorizationDate = new Date();
			userAuthorizing = Users.getCurrent();
		}
	}
	
	public BigDecimal getRealValue() throws Exception
	{
		BigDecimal realValue = BigDecimal.ZERO;
		for (TransactionAccount ta : transactionAccounts)
			if (ta.getDebitOrCredit().equals(Types.DebitOrCredit.CREDIT))
				realValue = realValue.add(ta.getValue());
		
		return realValue;
	}

	public Unity getOrigenUnity() {
		return origenUnity;
	}

	public void setOrigenUnity(Unity origenUnity) {
		this.origenUnity = origenUnity;
	}

	public Branch getOriginationBranch() {
		return originationBranch;
	}

	public void setOriginationBranch(Branch originationBranch) {
		this.originationBranch = originationBranch;
	}

	public Branch getDestinationBranch() {
		return destinationBranch;
	}

	public void setDestinationBranch(Branch destinationBranch) {
		this.destinationBranch = destinationBranch;
	}

}