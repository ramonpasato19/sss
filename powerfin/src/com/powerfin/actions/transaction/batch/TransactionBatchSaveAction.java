package com.powerfin.actions.transaction.batch;

import java.io.*;
import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.File;
import com.powerfin.util.*;

public class TransactionBatchSaveAction extends ViewBaseAction {

	private String subAction;

	@SuppressWarnings("unchecked")
	public void execute() throws Exception {

		Integer transactionBatchId = (Integer) getView().getValue("transactionBatchId");
		TransactionBatch transactionBatch = XPersistence.getManager().find(TransactionBatch.class, transactionBatchId);

		int lineNumber = 1;
		int row = 0;

		if (transactionBatch.getTransactionBatchStatus().getTransactionBatchStatusId().equals(TransactionBatchHelper.TRANSACTION_BATCH_IN_PROCESS_STATUS))
			throw new OperativeException("transaction_in_process");
		if (transactionBatch.getTransactionBatchStatus().getTransactionBatchStatusId().equals(TransactionBatchHelper.TRANSACTION_BATCH_FINISH_STATUS))
			throw new OperativeException("transaction_already_processed");
		if (subAction.equals("COLLECT")) {	
			
			transactionBatch.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthCollectedStatus());
			XPersistence.getManager().merge(transactionBatch);
			
			byte[] fileBytes = null;

			File oxfile = XPersistence.getManager().find(File.class, transactionBatch.getFile());

			fileBytes = oxfile.getData();
			String result = new String(fileBytes, "UTF-8");

			BufferedReader br = null;

			XPersistence.getManager()
					.createQuery("DELETE FROM TransactionBatchDetail o "
							+ "WHERE o.transactionBatch.transactionBatchId = :transactionBatchId ")
					.setParameter("transactionBatchId", transactionBatchId).executeUpdate();

			try {

				br = new BufferedReader(new StringReader(result));
				for (String line; (line = br.readLine()) != null;) {
					if (row > 0) {// informacion desde la 2da linea
						lineNumber++;
						System.out.println("line: " + line);

						TransactionBatchDetail tbd = new TransactionBatchDetail();
						tbd.setDetail(line);
						tbd.setLine(lineNumber);
						tbd.setTransactionBatch(transactionBatch);
						tbd.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthDetailCreateStatus());
						XPersistence.getManager().persist(tbd);
					}
					row++;
				}
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				try {
					if (br != null)
						br.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			getView().refresh();
			getView().refreshCollections();

			addMessage("complete_collection");

		} else if (subAction.equals("PROCESS")) {
						
			if (transactionBatch.getTransactionBatchStatus().getTransactionBatchStatusId().equals(TransactionBatchHelper.TRANSACTION_BATCH_REQUEST_STATUS))
				throw new OperativeException("please_collect_information");
			
			transactionBatch.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthInProcessStatus());
			XPersistence.getManager().merge(transactionBatch);
			
			String transactionModuleId = transactionBatch.getTransactionModule().getTransactionModuleId();
			
			List<String> batchDetailstatusToProcess = Arrays.asList(TransactionBatchHelper.TRANSACTION_BATCH_DETAIL_CREATE_STATUS, TransactionBatchHelper.TRANSACTION_BATCH_DETAIL_PROCESS_ERROR);
			
			List<TransactionBatchDetail> details = XPersistence.getManager().createQuery("SELECT tbd FROM TransactionBatchDetail tbd "
					+ "WHERE transactionBatch.transactionBatchId = :transactionBatchId "
					+ "AND transactionBatchStatus.transactionBatchStatusId IN :transactionBatchStatusId "
					+ "ORDER by line")
			.setParameter("transactionBatchId", transactionBatch.getTransactionBatchId())
			.setParameter("transactionBatchStatusId", batchDetailstatusToProcess)
			.getResultList();
			
			for (TransactionBatchDetail detailIte : details) {
				TransactionBatchDetail detail = XPersistence.getManager().find(TransactionBatchDetail.class, detailIte.getTransactionBatchDetailId());
				
				try {
					
					if (transactionModuleId.equals("PURCHASEPORTFOLIOPAYMENT"))
						processPurchasePortfolioPayment(transactionBatch, detail);
					else if (transactionModuleId.equals("SALEPORTFOLIOPAYMENT"))
						processSalePortfolioPayment(transactionBatch, detail);
					else if (transactionModuleId.equals("GENERALTRANSACTION"))
						processGeneralTransaction(transactionBatch, detail);
					else if (transactionModuleId.equals("INVOICESALEFUELSTATION"))
						processSaleInvoiceFuelStation(transactionBatch, detail);
					else if (transactionModuleId.equals("INVOICE_PURCHASE"))
						processPurchaseInvoice(transactionBatch, detail);
					else if (transactionModuleId.equals("INVOICE_SALE"))
						processSaleInvoice(transactionBatch, detail);
					else if (transactionModuleId.equals("TRANSFERRECEIVEDCOLLECTION"))
						processTransferReceivedCollectiond(transactionBatch, detail);
					else
						throw new OperativeException("transaction_module_not_found_for_batch_process");

					detail.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthDetailProcessStatus());
					XPersistence.getManager().merge(detail);

				} catch (Exception e) {
					e.printStackTrace();
					detail.setTransactionBatchStatus(
							TransactionBatchHelper.getTransactionBacthDetailProcessErrorStatus());
					detail.setErrorMessage(e.getMessage());
					XPersistence.getManager().merge(detail);
				}
				
				XPersistence.commit();
			}
			
			transactionBatch = XPersistence.getManager().find(TransactionBatch.class, transactionBatchId);
			transactionBatch.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthFinishStatus());
			XPersistence.getManager().merge(transactionBatch);
			XPersistence.commit();
			getView().refreshCollections();
			getView().refresh();
			addMessage("complete_process");
		}
	}

	public String getSubAction() {
		return subAction;
	}

	public void setSubAction(String subAction) {
		this.subAction = subAction;
	}

	public String validateDataLine(String[] dataLine) {
		return "";
	}

	private void processTransferReceivedCollectiond(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String accountBankId = (String)dataLine[0];
		String accountLoanId = (String)dataLine[1];
		BigDecimal value = null;
		String remark = null;
		
		Account accountBank = XPersistence.getManager().find(Account.class, accountBankId);
		if (accountBank == null)
			throw new OperativeException("account_bank_not_found", accountBankId);
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, accountLoanId);
		if (accountLoan == null)
			throw new OperativeException("account_loan_not_found", accountLoanId);
		
		if (!accountLoan.getDisbursementAccount().getCurrency().getCurrencyId().equals(accountBank.getCurrency().getCurrencyId()))
			throw new OperativeException("accounts_have_different_currency");
		
		if (accountLoan.getDisbursementAccount() == null)
			throw new OperativeException("account_disbursement_not_found", accountLoanId);
		
		try
		{
			value = new BigDecimal(dataLine[2]);
		}
		catch (Exception e)
		{
			throw new OperativeException("wrong_value", e.getMessage());
		}

		try
		{
			remark = (String)dataLine[3];
		}
		catch (Exception e)
		{
			throw new OperativeException("wrong_remark", e.getMessage());
		}
		
		if (remark == null || remark.trim().isEmpty())
			throw new OperativeException("remark_is_required");
		
		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(value);
		transaction.setRemark(remark);
		transaction.setCreditAccount(accountLoan.getDisbursementAccount());
		transaction.setDebitAccount(accountBank);
		transaction.setCurrency(accountBank.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getDisbursementAccount(), value, transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(accountBank, value, transaction));
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);
	}
	
	private void processPurchasePortfolioPayment(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = new BigDecimal(dataLine[3]);

		Account account = XPersistence.getManager().find(Account.class, dataLine[2]);
		if (account==null)
			throw new OperativeException("account_not_found",dataLine[2]);
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
		Account brokerAccount = XPersistence.getManager().find(Account.class, dataLine[1]);

		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(dataLine[2]);
		transaction.setCreditAccount(account);
		transaction.setDebitAccount(brokerAccount);
		transaction.setCurrency(account.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = AccountLoanHelper
				.getTransactionAccountsForAccountLoanPayment(transaction, accountLoan, brokerAccount,
						transactionValue);

		TransactionHelper.processTransaction(transaction, transactionAccounts);
		AccountLoanHelper.postAccountLoanPaymentSaveAction(transaction);
	}
	
	private void processSalePortfolioPayment(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		Integer subAccount = new Integer(dataLine[3]);
		BigDecimal capital = new BigDecimal(dataLine[4]);
		BigDecimal interest = new BigDecimal(dataLine[5]);
		BigDecimal defaultInterest = new BigDecimal(dataLine[6]);
		transactionValue = transactionValue.add(capital).add(interest).add(defaultInterest);

		Account account = XPersistence.getManager().find(Account.class, dataLine[2]);
		if (account==null)
			throw new OperativeException("account_not_found",dataLine[2]);
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
		Account brokerAccount = XPersistence.getManager().find(Account.class, dataLine[1]);

		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(dataLine[2]);
		transaction.setDebitAccount(account);
		transaction.setCreditAccount(brokerAccount);
		transaction.setCurrency(account.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = AccountLoanHelper
				.getTransactionAccountsForBatchPymentSalePortfolio(transaction, accountLoan, brokerAccount,
						subAccount, capital, interest, defaultInterest);

		TransactionHelper.processTransaction(transaction, transactionAccounts);
		AccountLoanHelper.postSalePortfolioPaymentSaveAction(transaction);
	}
	
	private void processGeneralTransaction(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String transactionModuleId = dataLine[0];
		String debitAccountId = dataLine[1];
		String categoryDebitAccountId = dataLine[2];
		String debitSubaccount = dataLine[3];
		String creditAccountId = dataLine[4];
		String categoryCreditAccountId = dataLine[5];
		String creditSubaccount = dataLine[6];
		String value = dataLine[7];
		String remark = dataLine[8];
		
		BigDecimal transactionValue = new BigDecimal(value);

		TransactionModule transactionModule = XPersistence.getManager().find(TransactionModule.class, transactionModuleId);
		Account debitAccount = XPersistence.getManager().find(Account.class, debitAccountId);
		Account creditAccount = XPersistence.getManager().find(Account.class, creditAccountId);
		
		Category categoryDebitAccount = XPersistence.getManager().find(Category.class, categoryDebitAccountId);
		Category categoryCreditAccount = XPersistence.getManager().find(Category.class, categoryCreditAccountId);
		
		if (transactionModule==null)
			throw new OperativeException("transaction_module_not_found_for_batch_process", transactionModuleId);
		if (debitAccount==null)
			throw new OperativeException("account_not_found", debitAccountId);
		if (creditAccount==null)
			throw new OperativeException("account_not_found", creditAccountId);
		if (categoryDebitAccount==null)
			throw new OperativeException("category_not_found", categoryDebitAccountId);
		if (categoryCreditAccount==null)
			throw new OperativeException("category_not_found", categoryCreditAccountId);
		if (!debitAccount.getCurrency().getCurrencyId().equals(creditAccount.getCurrency().getCurrencyId()))
			throw new OperativeException("accounts_have_different_currency");
		if (transactionValue.compareTo(BigDecimal.ZERO)<0)
			throw new OperativeException("value_must_be_greater_than_zero", value);
		
		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionModule);
		transaction.setTransactionStatus(transactionModule.getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remark);
		transaction.setCreditAccount(creditAccount);
		transaction.setDebitAccount(debitAccount);
		transaction.setCurrency(creditAccount.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, Integer.parseInt(creditSubaccount), transactionValue, transaction, categoryCreditAccount, null));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, Integer.parseInt(debitSubaccount), transactionValue, transaction, categoryDebitAccount, null));

		TransactionHelper.processTransaction(transaction, transactionAccounts);
	}
	
	@SuppressWarnings("unchecked")
	private void processSaleInvoiceFuelStation(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		String remark = "";
		Person person = null;
		String personExternalCode = new String(dataLine[0]);
		String identification = new String(dataLine[1]);
		String personName = new String(dataLine[2]);
		String invoiceExternalCode = new String(dataLine[3]);
		String issueDateString = new String(dataLine[4]);
		Date issueDate = UtilApp.stringToDate(issueDateString);
		String hour = new String(dataLine[5]);
		String establishmentCode = new String(dataLine[6]);
		String emissionPointCode = new String(dataLine[7]);
		String sequentialCode = new String(dataLine[8]);
		String authorizationCode = new String(dataLine[9]);
		String dispenser = new String(dataLine[10]);
		String externalProduct = new String(dataLine[11]);
		String accountItemId = new String(dataLine[12]);
		BigDecimal quantity = new BigDecimal(dataLine[13]);
		BigDecimal unitPrice = new BigDecimal(dataLine[14]);
		BigDecimal amount = new BigDecimal(dataLine[15]);
		BigDecimal taxAmount = new BigDecimal(dataLine[16]);
		BigDecimal total = new BigDecimal(dataLine[17]);
		String paymentType = new String(dataLine[18]);
		String productId = new String(dataLine[19]);
		String boxAccountId = new String(dataLine[20]);
		Integer branchId = new Integer(dataLine[21]);
		/*
		Integer lastDetail = new Integer(dataLine[22]);
		Integer authorizeInvoice = new Integer(dataLine[23]);
		Integer accountingCostOfSale = new Integer(dataLine[24]);
		*/
		
		Integer authorizeInvoice = 1;
		Integer accountingCostOfSale = 1;
		
		transactionValue = total;
		if (paymentType.equals("CC"))
		{
			remark = "DESPACHO COMBUSTIBLE: ";
			boxAccountId = null;
		}
		else if (paymentType.equals("EF"))
			remark = "VENTA DE COMBUSTIBLE - EFECTIVO: ";
		else if (paymentType.equals("TJ"))
			remark = "VENTA DE COMBUSTIBLE - TARJETA: ";
		else if (paymentType.equals("CH"))
			remark = "VENTA DE COMBUSTIBLE - CHEQUE: ";
		else if (paymentType.equals("CA"))
		{
			remark = "CALIBRACION DE COMBUSTIBLE: ";
			accountingCostOfSale = 0;
			boxAccountId = null;
		}
		else if (paymentType.equals("CP"))
			remark = "VENTA DE COMBUSTIBLE - PREPAGO: ";
		else
			remark = "VENTA DE COMBUSTIBLE - OTRO: ";
		
		remark += externalProduct+", FECHA: "+issueDateString+" "+hour+", DISPENSADOR: "+dispenser;
		
		String reamrkPayment = "COBRO COMPROBANTE DE VENTA: "+invoiceExternalCode+", COMBUSTIBLE: "+externalProduct+", FECHA: "+issueDateString+" "+hour+", DISPENSADOR: "+dispenser;
		Account accountItem = XPersistence.getManager().find(Account.class, accountItemId);
		if (accountItem == null)
			throw new OperativeException("account_item_not_found", accountItemId);
		
		List<Person> persons = XPersistence.getManager().createQuery("SELECT o FROM Person o "
				+ "WHERE o.identification = :identification")
			.setParameter("identification", identification)
			.getResultList();

		if(!persons.isEmpty())
			person = persons.get(0);
		else
		{
			person = new Person();
			person.setIdentification(identification);
			if (identification.length()==7)
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "PLA"));
			else
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "CED"));
			person.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.NATURAL_PERSON));
			person.setName(personName);
			person.setExternalCode(personExternalCode);
			XPersistence.getManager().persist(person);
			
			NaturalPerson naturalPerson = new NaturalPerson();
			naturalPerson.setGender(XPersistence.getManager().find(Gender.class, "M"));
			naturalPerson.setPersonId(person.getPersonId());
			naturalPerson.setFirstName(".");
			naturalPerson.setPaternalSurname(personName);
			naturalPerson.setIdentification(identification);
			naturalPerson.setIdentificationType(person.getIdentificationType());
			naturalPerson.setMaritalStatus(XPersistence.getManager().find(MaritalStatus.class, "999"));
			XPersistence.getManager().persist(naturalPerson);
			
		}
		
		Account account = AccountHelper.createAccount(productId, person.getPersonId(), null, null, invoiceExternalCode, null, branchId);
		
		AccountInvoice accountInvoice = new AccountInvoice();
		accountInvoice.setAccountId(account.getAccountId());
		accountInvoice.setAccount(account);
		accountInvoice.setPerson(person);
		accountInvoice.setProduct(account.getProduct());
		accountInvoice.setIssueDate(issueDate);
		InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
		accountInvoice.setInvoiceVoucherType(typeInvoice);
		accountInvoice.setRemark(remark);
		accountInvoice.setEstablishmentCode(establishmentCode);
		accountInvoice.setEmissionPointCode(emissionPointCode);
		accountInvoice.setSequentialCode(sequentialCode);
		accountInvoice.setAuthorizationCode(authorizationCode);
		Unity unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
		accountInvoice.setUnity(unity);
		XPersistence.getManager().persist(accountInvoice);
		
		AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
		accountInvoiceDetail.setAccountInvoice(accountInvoice);
		accountInvoiceDetail.setAccountDetail(accountItem);
		Tax tax = null;
		if (taxAmount.compareTo(BigDecimal.ZERO) > 0)
		{
			tax = XPersistence.getManager().find(Tax.class, XPersistence.getManager().find(Parameter.class, "IVA_PERCENTAGE").getValue());
			accountInvoiceDetail.setTaxAmount(taxAmount);
		}
		else
		{
			tax = XPersistence.getManager().find(Tax.class, "IVA0");
			accountInvoiceDetail.setTaxAmount(BigDecimal.ZERO);
		}
		accountInvoiceDetail.setTax(tax);
		accountInvoiceDetail.setQuantity(quantity);
		accountInvoiceDetail.setUnitPrice(unitPrice);
		accountInvoiceDetail.setDiscount(BigDecimal.ZERO);
		accountInvoiceDetail.setAmount(amount);
		accountInvoiceDetail.setFinalAmount(total);
		XPersistence.getManager().persist(accountInvoiceDetail);
		
		List<AccountInvoiceDetail> details =  new ArrayList<AccountInvoiceDetail>();
		details.add(accountInvoiceDetail);
		accountInvoice.setDetails(details);
		
		//InvoiceTransaction
		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remark);
		transaction.setDebitAccount(account);
		transaction.setCurrency(account.getCurrency());
		transaction.setOrigenUnity(unity);
		
		XPersistence.getManager().persist(transaction);
		
		//AuthorizeTransaction
		if (authorizeInvoice == 1)
		{
			List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoiceSale(transaction, accountingCostOfSale);
			
			TransactionHelper.processTransaction(transaction, transactionAccounts);
	
			AccountInvoiceHelper.postInvoiceSaleSaveAction(transaction);
	
			//PaymentInvoiceTransaction
			Account boxAccount = boxAccountId!=null?XPersistence.getManager().find(Account.class, boxAccountId):null;
			if (boxAccount != null)
			{
				TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, "INVOICESALEPAYMENT");
				transaction = TransactionHelper.getNewInitTransaction();
				transaction.setTransactionModule(tm);
				transaction.setTransactionStatus(tm.getFinancialTransactionStatus());
				transaction.setValue(transactionValue);
				transaction.setRemark(reamrkPayment);
				transaction.setCreditAccount(account);
				transaction.setDebitAccount(boxAccount);
				transaction.setCurrency(account.getCurrency());
				transaction.setOrigenUnity(unity);
				
				XPersistence.getManager().persist(transaction);
				
				transactionAccounts = new ArrayList<TransactionAccount>();
				
				transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(account, accountInvoice.getCalculateTotal(), transaction));
				transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(boxAccount, accountInvoice.getCalculateTotal(), transaction));
		
				TransactionHelper.processTransaction(transaction, transactionAccounts);
				
				AccountInvoiceHelper.postInvoiceSalePaymentSaveAction(transaction);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void processPurchaseInvoice(TransactionBatch transactionBatch, TransactionBatchDetail detail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		String remark = "";
		Person person = null;
		String personExternalCode = new String(dataLine[0]);
		String identification = new String(dataLine[1]);
		String personName = new String(dataLine[2]);
		String invoiceExternalCode = new String(dataLine[3]);
		String issueDateString = new String(dataLine[4]);
		Date issueDate = UtilApp.stringToDate(issueDateString);
		String hour = new String(dataLine[5]);
		String establishmentCode = new String(dataLine[6]);
		String emissionPointCode = new String(dataLine[7]);
		String sequentialCode = new String(dataLine[8]);
		String authorizationCode = new String(dataLine[9]);
		String dispenser = new String(dataLine[10]);
		String externalProduct = new String(dataLine[11]);
		String accountItemId = new String(dataLine[12]);
		BigDecimal quantity = new BigDecimal(dataLine[13]);
		BigDecimal unitPrice = new BigDecimal(dataLine[14]);
		BigDecimal amount = new BigDecimal(dataLine[15]);
		BigDecimal taxAmount = new BigDecimal(dataLine[16]);
		BigDecimal total = new BigDecimal(dataLine[17]);
		String paymentType = new String(dataLine[18]);
		String productId = new String(dataLine[19]);
		String boxAccountId = new String(dataLine[20]);
		
		transactionValue = total;
		remark = "COMPRA DE PRODUCTOS: "+externalProduct+", FECHA: "+issueDateString+" "+hour;
		String reamrkPayment = "PAGO FACT: "+invoiceExternalCode+", PRODUCTO: "+externalProduct+", FECHA: "+issueDateString+" "+hour;
		Account accountItem = XPersistence.getManager().find(Account.class, accountItemId);
		if (accountItem == null)
			throw new OperativeException("account_item_not_found", accountItemId);
		
		List<Person> persons = XPersistence.getManager().createQuery("SELECT o FROM Person o "
				 +"WHERE o.identification = :identification")
			.setParameter("identification", identification)
			.getResultList();

		if(!persons.isEmpty())
			person = persons.get(0);
		else
		{
			person = new Person();
			person.setIdentification(identification);
			person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "CED"));
			person.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.NATURAL_PERSON));
			person.setName(personName);
			person.setExternalCode(personExternalCode);
			XPersistence.getManager().persist(person);
			
			NaturalPerson naturalPerson = new NaturalPerson();
			naturalPerson.setGender(XPersistence.getManager().find(Gender.class, "M"));
			naturalPerson.setPersonId(person.getPersonId());
			naturalPerson.setFirstName(".");
			naturalPerson.setPaternalSurname(personName);
			naturalPerson.setIdentification(identification);
			naturalPerson.setIdentificationType(person.getIdentificationType());
			naturalPerson.setMaritalStatus(XPersistence.getManager().find(MaritalStatus.class, "999"));
			XPersistence.getManager().persist(naturalPerson);
			
		}
		
		Account account = AccountHelper.createAccount(productId, person.getPersonId(), null, null, invoiceExternalCode, null);
		
		AccountInvoice accountInvoice = new AccountInvoice();
		accountInvoice.setAccountId(account.getAccountId());
		accountInvoice.setAccount(account);
		accountInvoice.setPerson(person);
		accountInvoice.setProduct(account.getProduct());
		accountInvoice.setIssueDate(issueDate);
		InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
		accountInvoice.setInvoiceVoucherType(typeInvoice);
		accountInvoice.setRemark(remark);
		accountInvoice.setEstablishmentCode(establishmentCode);
		accountInvoice.setEmissionPointCode(emissionPointCode);
		accountInvoice.setSequentialCode(sequentialCode);
		accountInvoice.setAuthorizationCode(authorizationCode);
		Unity unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
		accountInvoice.setUnity(unity);
		XPersistence.getManager().persist(accountInvoice);
		
		AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
		accountInvoiceDetail.setAccountInvoice(accountInvoice);
		accountInvoiceDetail.setAccountDetail(accountItem);
		Tax tax = null;
		if (taxAmount.compareTo(BigDecimal.ZERO) > 0)
		{
			tax = XPersistence.getManager().find(Tax.class, XPersistence.getManager().find(Parameter.class, "IVA_PERCENTAGE").getValue());
			accountInvoiceDetail.setTaxAmount(taxAmount);
		}
		else
		{
			tax = XPersistence.getManager().find(Tax.class, "IVA0");
			accountInvoiceDetail.setTaxAmount(BigDecimal.ZERO);
		}
		accountInvoiceDetail.setTax(tax);
		accountInvoiceDetail.setQuantity(quantity);
		accountInvoiceDetail.setUnitPrice(unitPrice);
		accountInvoiceDetail.setDiscount(BigDecimal.ZERO);
		accountInvoiceDetail.setAmount(amount);
		accountInvoiceDetail.setFinalAmount(total);
		XPersistence.getManager().persist(accountInvoiceDetail);
		
		List<AccountInvoiceDetail> details =  new ArrayList<AccountInvoiceDetail>();
		details.add(accountInvoiceDetail);
		accountInvoice.setDetails(details);
		
		//InvoiceTransaction
		Transaction transaction = TransactionHelper.getNewInitTransaction();
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remark);
		transaction.setCreditAccount(account);
		transaction.setCurrency(account.getCurrency());
		transaction.setOrigenUnity(unity);
		
		XPersistence.getManager().persist(transaction);
		
		List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoicePurchase(transaction);
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);

		AccountInvoiceHelper.postInvoicePurchaseSaveAction(transaction);
		
		//PaymentInvoiceTransaction
		Account boxAccount = XPersistence.getManager().find(Account.class, boxAccountId);
		if (boxAccount != null)
		{
			TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, "TRANSFERDUE");
			transaction = TransactionHelper.getNewInitTransaction();
			transaction.setTransactionModule(tm);
			transaction.setTransactionStatus(tm.getFinancialTransactionStatus());
			transaction.setValue(transactionValue);
			transaction.setRemark(reamrkPayment);
			transaction.setDebitAccount(account);
			transaction.setCreditAccount(boxAccount);
			transaction.setCurrency(account.getCurrency());
			transaction.setOrigenUnity(unity);
			
			XPersistence.getManager().persist(transaction);
			
			transactionAccounts = new ArrayList<TransactionAccount>();
			
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, accountInvoice.getCalculateTotal(), transaction));
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(boxAccount, accountInvoice.getCalculateTotal(), transaction));
	
			TransactionHelper.processTransaction(transaction, transactionAccounts);
			
			AccountInvoiceHelper.postInvoicePurchasePaymentSaveAction(transaction);
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void processSaleInvoice(TransactionBatch transactionBatch, TransactionBatchDetail detail)//, TransactionBatchDetail oldDetail)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		boolean newInvoice = true;
		
		dataLine = detail.getDetail().split(delimiter);

		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		String remark = "";
		
		Person person = null;
		Account account = null;
		AccountInvoice accountInvoice = null;
		Unity unity = null;
		
		String personExternalCode = new String(dataLine[0]);
		String identification = new String(dataLine[1]);
		String personName = new String(dataLine[2]);
		String invoiceExternalCode = new String(dataLine[3]);
		String issueDateString = new String(dataLine[4]);
		Date issueDate = UtilApp.stringToDate(issueDateString);
		String hour = new String(dataLine[5]);
		String establishmentCode = new String(dataLine[6]);
		String emissionPointCode = new String(dataLine[7]);
		String sequentialCode = new String(dataLine[8]);
		String authorizationCode = new String(dataLine[9]);
		String dispenser = new String(dataLine[10]);
		String externalProduct = new String(dataLine[11]);
		String accountItemId = new String(dataLine[12]);
		BigDecimal quantity = new BigDecimal(dataLine[13]);
		BigDecimal unitPrice = new BigDecimal(dataLine[14]);
		BigDecimal amount = new BigDecimal(dataLine[15]);
		BigDecimal taxAmount = new BigDecimal(dataLine[16]);
		BigDecimal total = new BigDecimal(dataLine[17]);
		String paymentType = new String(dataLine[18]);
		String productId = new String(dataLine[19]);
		String boxAccountId = new String(dataLine[20]);
		Integer branchId = new Integer(dataLine[21]);
		Integer lastDetail = new Integer(dataLine[22]);
		Integer authorizeInvoice = new Integer(dataLine[23]);
		Integer accountingCostOfSale = new Integer(dataLine[24]);
		
		List<Person> persons = XPersistence.getManager().createQuery("SELECT o FROM Person o "
				+ "WHERE o.identification = :identification")
			.setParameter("identification", identification)
			.getResultList();

		if(!persons.isEmpty())
			person = persons.get(0);
		else
		{
			person = new Person();
			person.setIdentification(identification);
			person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "CED"));
			person.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.NATURAL_PERSON));
			person.setName(personName);
			person.setExternalCode(personExternalCode);
			XPersistence.getManager().persist(person);
			
			NaturalPerson naturalPerson = new NaturalPerson();
			naturalPerson.setGender(XPersistence.getManager().find(Gender.class, "M"));
			naturalPerson.setPersonId(person.getPersonId());
			naturalPerson.setFirstName(".");
			naturalPerson.setPaternalSurname(personName);
			naturalPerson.setIdentification(identification);
			naturalPerson.setIdentificationType(person.getIdentificationType());
			naturalPerson.setMaritalStatus(XPersistence.getManager().find(MaritalStatus.class, "999"));
			XPersistence.getManager().persist(naturalPerson);
			
		}
		
		List<Account> invoices = XPersistence.getManager().createQuery(
				"SELECT a FROM Account a "
				+ "WHERE a.code = :code "
				+ "AND a.person.personId = :personId "
				+ "AND a.product.productId = :productId ")
				.setParameter("code", invoiceExternalCode)
				.setParameter("personId", person.getPersonId())
				.setParameter("productId", productId)
				.getResultList();
		
		if(!invoices.isEmpty())
		{
			account = invoices.get(0);
			accountInvoice = (AccountInvoice) XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
			unity = accountInvoice.getUnity();
			newInvoice = false;
		}
		
		transactionValue = total;
		remark = "VENTA PRODUCTOS, FACTURA: "+invoiceExternalCode+", CAJA: "+dispenser+", FECHA: "+issueDateString+" "+hour+", SUCURSAL: "+branchId;
		String reamrkPayment = "COBRO FACT: "+invoiceExternalCode+", CAJA: "+dispenser+", FECHA: "+issueDateString+" "+hour;
		
		Account accountItem = XPersistence.getManager().find(Account.class, accountItemId);
		if (accountItem == null)
		{
			List<Account> accountItems = XPersistence.getManager().createQuery("SELECT o FROM Account o "
					+ "WHERE o.code = :code")
				.setParameter("code", externalProduct)
				.getResultList();

			if(!accountItems.isEmpty())
				accountItem = accountItems.get(0);
			else
				throw new OperativeException("account_item_not_found", externalProduct, accountItemId);
		}
		
		if (newInvoice)
		{
			account = AccountHelper.createAccount(productId, person.getPersonId(), null, null, invoiceExternalCode, null, branchId);
			accountInvoice = new AccountInvoice();
			accountInvoice.setAccountId(account.getAccountId());
			accountInvoice.setAccount(account);
			accountInvoice.setPerson(person);
			accountInvoice.setProduct(account.getProduct());
			accountInvoice.setIssueDate(issueDate);
			InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
			accountInvoice.setInvoiceVoucherType(typeInvoice);
			accountInvoice.setRemark(remark);
			accountInvoice.setEstablishmentCode(establishmentCode);
			accountInvoice.setEmissionPointCode(emissionPointCode);
			accountInvoice.setSequentialCode(sequentialCode);
			accountInvoice.setAuthorizationCode(authorizationCode);
			unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
			accountInvoice.setUnity(unity);
			XPersistence.getManager().persist(accountInvoice);
		}

		
		AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
		accountInvoiceDetail.setAccountInvoice(accountInvoice);
		accountInvoiceDetail.setAccountDetail(accountItem);
		Tax tax = null;
		if (taxAmount.compareTo(BigDecimal.ZERO) > 0)
		{
			tax = XPersistence.getManager().find(Tax.class, XPersistence.getManager().find(Parameter.class, "IVA_PERCENTAGE").getValue());
			accountInvoiceDetail.setTaxAmount(taxAmount);
		}
		else
		{
			tax = XPersistence.getManager().find(Tax.class, "IVA0");
			accountInvoiceDetail.setTaxAmount(BigDecimal.ZERO);
		}
		accountInvoiceDetail.setTax(tax);
		accountInvoiceDetail.setQuantity(quantity);
		accountInvoiceDetail.setUnitPrice(unitPrice);
		accountInvoiceDetail.setDiscount(BigDecimal.ZERO);
		accountInvoiceDetail.setAmount(amount);
		accountInvoiceDetail.setFinalAmount(total);
		XPersistence.getManager().persist(accountInvoiceDetail);
		
		accountInvoice.setDetails(XPersistence.getManager().createQuery("SELECT o FROM AccountInvoiceDetail o "
				+ "WHERE o.accountInvoice.accountId = :accountInvoiceId")
				.setParameter("accountInvoiceId", accountInvoice.getAccountId())
				.getResultList());		
		
		//InvoiceTransaction
		if (lastDetail == 1)
		{
			Transaction transaction = TransactionHelper.getNewInitTransaction();
			transaction.setTransactionModule(transactionBatch.getTransactionModule());
			if (authorizeInvoice == 1)
				transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
			else
				transaction.setTransactionStatus(transactionBatch.getTransactionModule().getDefaultTransactionStatus());
			transaction.setValue(transactionValue);
			transaction.setRemark(remark);
			transaction.setDebitAccount(account);
			transaction.setCurrency(account.getCurrency());
			transaction.setOrigenUnity(unity);
			
			XPersistence.getManager().persist(transaction);
						
			if (authorizeInvoice == 1)
			{
				List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoiceSale(transaction, accountingCostOfSale);
				
				TransactionHelper.processTransaction(transaction, transactionAccounts);
		
				AccountInvoiceHelper.postInvoiceSaleSaveAction(transaction);
		
				//PaymentInvoiceTransaction
				Account boxAccount = XPersistence.getManager().find(Account.class, boxAccountId);
				if (boxAccount != null)
				{
					TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, "INVOICESALEPAYMENT");
					transaction = TransactionHelper.getNewInitTransaction();
					transaction.setTransactionModule(tm);
					transaction.setTransactionStatus(tm.getFinancialTransactionStatus());
					transaction.setValue(transactionValue);
					transaction.setRemark(reamrkPayment);
					transaction.setCreditAccount(account);
					transaction.setDebitAccount(boxAccount);
					transaction.setCurrency(account.getCurrency());
					transaction.setOrigenUnity(unity);
					
					XPersistence.getManager().persist(transaction);
					
					transactionAccounts = new ArrayList<TransactionAccount>();
					
					transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(account, accountInvoice.getCalculateTotal(), transaction));
					transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(boxAccount, accountInvoice.getCalculateTotal(), transaction));
			
					TransactionHelper.processTransaction(transaction, transactionAccounts);
					
					AccountInvoiceHelper.postInvoiceSalePaymentSaveAction(transaction);
				}
			}
		}
	}
}
