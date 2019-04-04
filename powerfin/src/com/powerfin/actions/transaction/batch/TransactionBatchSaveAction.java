package com.powerfin.actions.transaction.batch;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.util.XavaResources;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.AccountHelper;
import com.powerfin.helper.AccountInvoiceHelper;
import com.powerfin.helper.AccountLoanHelper;
import com.powerfin.helper.JPAHelper;
import com.powerfin.helper.ParameterHelper;
import com.powerfin.helper.PersonHelper;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.helper.TransactionBatchHelper;
import com.powerfin.helper.TransactionHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountInvoiceDetail;
import com.powerfin.model.AccountLoan;
import com.powerfin.model.Branch;
import com.powerfin.model.Category;
import com.powerfin.model.File;
import com.powerfin.model.Gender;
import com.powerfin.model.IdentificationType;
import com.powerfin.model.InvoiceVoucherType;
import com.powerfin.model.LegalPerson;
import com.powerfin.model.MaritalStatus;
import com.powerfin.model.NaturalPerson;
import com.powerfin.model.Parameter;
import com.powerfin.model.Person;
import com.powerfin.model.PersonType;
import com.powerfin.model.Tax;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.model.TransactionBatch;
import com.powerfin.model.TransactionBatchDetail;
import com.powerfin.model.TransactionModule;
import com.powerfin.model.Unity;
import com.powerfin.model.types.Types;
import com.powerfin.util.UtilApp;

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
				Transaction transaction = TransactionHelper.getNewInitTransaction();
				try {
					
					if (transactionModuleId.equals("PURCHASEPORTFOLIOPAYMENT"))
						processPurchasePortfolioCollection(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("SALEPORTFOLIOPAYMENT"))
						processSalePortfolioPayment(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("GENERALTRANSACTION"))
						processGeneralTransaction(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("ADVANCEDGENERALTRANSACTION"))
						processAdvancedGeneralTransaction(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("INVOICESALEFUELSTATION"))
						processRegisterSaleInvoiceFuelStation(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("POSTINVOICESALEFUELSTATION"))
						processAccountingSaleInvoiceFuelStation(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("POSTINVOICEPURCHASE"))
						processAccountingPurchaseInvoice(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("POSTINVOICESALE"))
						processAccountingSaleInvoice(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("INVOICESALEPAYMENT"))
						processSaleInvoicePayment(transactionBatch, detail, transaction);
					
					else if (transactionModuleId.equals("TRANSFERFORLOANCOLLECTION"))
						processTransferForLoanCollection(transactionBatch, detail, transaction);
					
					else
						throw new OperativeException("transaction_module_not_found_for_batch_process");

					detail.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthDetailProcessStatus());
					detail.setErrorMessage(null);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					
					XPersistence.rollback();
					
					detail.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthDetailProcessErrorStatus());
					detail.setErrorMessage(e.getMessage());
					if (transaction!=null && transaction.getTransactionId()!=null)
					{
						transaction.setTransactionStatus(TransactionHelper.getTransactionStatusByStatusId(TransactionHelper.TRANSACTION_ANNULLED_STATUS_ID));
						detail.setErrorMessage(transaction.getVoucher()+": "+detail.getErrorMessage());
						XPersistence.getManager().merge(transaction);
						
					}
				}
				
				XPersistence.getManager().merge(detail);
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

	private void processTransferForLoanCollection(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String debitAccountId = (String)dataLine[0];
		String accountLoanId = (String)dataLine[1];
		BigDecimal value = null;
		String remark = null;
		
		Account debitAccount = XPersistence.getManager().find(Account.class, debitAccountId);
		if (debitAccount == null)
			throw new OperativeException("account_bank_not_found", debitAccountId);
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, accountLoanId);
		if (accountLoan == null)
			throw new OperativeException("account_loan_not_found", accountLoanId);
		
		if (!accountLoan.getDisbursementAccount().getCurrency().getCurrencyId().equals(debitAccount.getCurrency().getCurrencyId()))
			throw new OperativeException("accounts_have_different_currency");
		
		if (accountLoan.getDisbursementAccount() == null)
			throw new OperativeException("account_disbursement_not_found", accountLoanId);
		
		if (UtilApp.isValidDecimalNumber(dataLine[2]))
			value = new BigDecimal(dataLine[2]);
		else
			throw new OperativeException("wrong_value", dataLine[2]);

		remark = XavaResources.getString("transfer_to_loan_collection", accountLoanId, accountLoan.getDisbursementAccount().getAccountId(), debitAccountId);
		
		try
		{
			if (dataLine[3]!=null && ((String)dataLine[3]).length()>0)
				remark = remark + ", " + (String)dataLine[3];
		}catch(ArrayIndexOutOfBoundsException ex) {}
		
		if (UtilApp.fieldIsEmpty(remark))
			throw new OperativeException("remark_is_required");
		
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(value);
		transaction.setRemark(remark);
		transaction.setCreditAccount(accountLoan.getDisbursementAccount());
		transaction.setDebitAccount(debitAccount);
		transaction.setCurrency(debitAccount.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getDisbursementAccount(), value, transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, value, transaction));
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);
	}
	
	private void processPurchasePortfolioCollection(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = null;
		Date valueDate = null;

		Account account = XPersistence.getManager().find(Account.class, dataLine[2]);
		if (account==null)
			throw new OperativeException("account_not_found",dataLine[2]);
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
		if (accountLoan.getDisbursementAccount() == null)
			throw new OperativeException("account_disbursement_not_found", account.getAccountId());
		
		if (!accountLoan.getDisbursementAccount().getCurrency().getCurrencyId().equals(account.getCurrency().getCurrencyId()))
			throw new OperativeException("accounts_have_different_currency");
		
		if (UtilApp.isValidDecimalNumber(dataLine[3]))
			transactionValue = new BigDecimal(dataLine[3]);
		else
			throw new OperativeException("wrong_value", dataLine[3]);
		
		try
		{
			if (dataLine[4]!=null)
				valueDate = UtilApp.stringToDate(dataLine[4]);
		}catch(ArrayIndexOutOfBoundsException ex) {}
		
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(dataLine[2]);
		transaction.setCreditAccount(account);
		transaction.setDebitAccount(accountLoan.getDisbursementAccount());
		transaction.setCurrency(account.getCurrency());
		transaction.setValueDate(valueDate!=null?valueDate:null);

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = AccountLoanHelper
				.getTransactionAccountsForAccountLoanPayment(transaction, accountLoan, null,
						transactionValue);

		TransactionHelper.processTransaction(transaction, transactionAccounts);
		AccountLoanHelper.postAccountLoanPaymentSaveAction(transaction);
	}
	
	private void processSalePortfolioPayment(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		
		if (!UtilApp.isValidDecimalNumber(dataLine[4]))
			throw new OperativeException("wrong_capital_value", dataLine[4]);
		
		if (!UtilApp.isValidDecimalNumber(dataLine[5]))
			throw new OperativeException("wrong_interest_value", dataLine[5]);
		
		if (!UtilApp.isValidDecimalNumber(dataLine[6]))
			throw new OperativeException("wrong_default_interest_value", dataLine[6]);
		
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
		if (brokerAccount==null)
			throw new OperativeException("broker_account_not_found",dataLine[1]);
		
		if (!accountLoan.getDisbursementAccount().getCurrency().getCurrencyId().equals(brokerAccount.getCurrency().getCurrencyId()))
			throw new OperativeException("accounts_have_different_currency");
		
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
	
	private void processAdvancedGeneralTransaction(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String transactionModuleId = dataLine[0];
		
		String debitAccountId = dataLine[1];
		String categoryDebitAccountId = dataLine[2];
		String debitSubaccount = dataLine[3];
		String debitBranchId = dataLine[4];
		String debitQuantityStr = dataLine[5];
		
		String creditAccountId = dataLine[6];
		String categoryCreditAccountId = dataLine[7];
		String creditSubaccount = dataLine[8];
		String creditBranchId = dataLine[9];
		String creditQuantityStr = dataLine[10];
		
		String value = dataLine[11];
		String remark = dataLine[12];
		
		BigDecimal transactionValue = null;
		BigDecimal debitQuantity = null;
		BigDecimal creditQuantity = null;
		
		if (UtilApp.isValidDecimalNumber(value))
			transactionValue = new BigDecimal(value);
		else
			throw new OperativeException("wrong_value", value);

		if (UtilApp.isValidDecimalNumber(debitQuantityStr))
			debitQuantity = new BigDecimal(debitQuantityStr);
		else
			throw new OperativeException("wrong_debit_quantity", debitQuantityStr);
		
		if (UtilApp.isValidDecimalNumber(creditQuantityStr))
			creditQuantity = new BigDecimal(creditQuantityStr);
		else
			throw new OperativeException("wrong_credit_quantity", creditQuantityStr);
		
		
		if (!UtilApp.isValidIntegerNumber(debitBranchId))
			throw new OperativeException("wrong_debit_branch", debitBranchId);
		
		if (!UtilApp.isValidIntegerNumber(creditBranchId))
			throw new OperativeException("wrong_credit_branch", creditBranchId);
		
		if (!UtilApp.isValidIntegerNumber(debitSubaccount))
			throw new OperativeException("wrong_debit_subaccount", debitSubaccount);
		
		if (!UtilApp.isValidIntegerNumber(creditSubaccount))
			throw new OperativeException("wrong_credit_subaccount", creditSubaccount);
		
		TransactionModule transactionModule = XPersistence.getManager().find(TransactionModule.class, transactionModuleId);
		Account debitAccount = XPersistence.getManager().find(Account.class, debitAccountId);
		Account creditAccount = XPersistence.getManager().find(Account.class, creditAccountId);
		
		Category categoryDebitAccount = XPersistence.getManager().find(Category.class, categoryDebitAccountId);
		Category categoryCreditAccount = XPersistence.getManager().find(Category.class, categoryCreditAccountId);
		
		Branch debitBranch = XPersistence.getManager().find(Branch.class, Integer.parseInt(debitBranchId));
		Branch creditBranch = XPersistence.getManager().find(Branch.class, Integer.parseInt(creditBranchId));
		
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
		if (debitBranch==null)
			throw new OperativeException("debit_branch_not_found", debitBranch);
		if (creditBranch==null)
			throw new OperativeException("credit_branch_not_found", creditBranch);
		
		transaction.setTransactionModule(transactionModule);
		transaction.setTransactionStatus(transactionModule.getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remark);
		transaction.setCreditAccount(creditAccount);
		transaction.setDebitAccount(debitAccount);
		transaction.setCurrency(creditAccount.getCurrency());

		XPersistence.getManager().persist(transaction);

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		transactionAccounts.add(TransactionAccountHelper.createTransactionAccount(
				creditAccount, 
				Integer.parseInt(creditSubaccount), 
				categoryCreditAccount, 
				Types.DebitOrCredit.CREDIT, 
				transactionValue, 
				creditQuantity,
				null,
				transaction, 
				null,
				null,
				null,
				creditBranch
				));

		transactionAccounts.add(TransactionAccountHelper.createTransactionAccount(
				debitAccount, 
				Integer.parseInt(debitSubaccount), 
				categoryDebitAccount, 
				Types.DebitOrCredit.DEBIT, 
				transactionValue, 
				debitQuantity,
				null,
				transaction, 
				null,
				null,
				null,
				debitBranch
				));

		TransactionHelper.processTransaction(transaction, transactionAccounts);
	}
	
	private void processGeneralTransaction(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
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
		
		BigDecimal transactionValue = null;
		
		if (UtilApp.isValidDecimalNumber(value))
			transactionValue = new BigDecimal(value);
		else
			throw new OperativeException("wrong_value", value);

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
	
	private void processRegisterSaleInvoiceFuelStation(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String remark = "";
		Person person = null;
		String personExternalCode = new String(dataLine[0]);
		String identification = new String(dataLine[1]);
		String personName = new String(dataLine[2]);
		String invoiceExternalCode = new String(dataLine[3]);
		String issueDateString = new String(dataLine[4]);
		String hour = new String(dataLine[5]);
		Date issueDate = UtilApp.stringToDate(issueDateString+" "+hour,"yyyy-MM-dd HH:mm:ss");
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
		Integer branchId = new Integer(dataLine[21]);
		
		if (paymentType.equals("CC"))
			remark = "DESPACHO COMBUSTIBLE: ";
		else if (paymentType.equals("EF"))
			remark = "VENTA DE COMBUSTIBLE - EFECTIVO: ";
		else if (paymentType.equals("TJ"))
			remark = "VENTA DE COMBUSTIBLE - TARJETA: ";
		else if (paymentType.equals("CH"))
			remark = "VENTA DE COMBUSTIBLE - CHEQUE: ";
		else if (paymentType.equals("CA"))
			remark = "CALIBRACION DE COMBUSTIBLE: ";
		else if (paymentType.equals("CP"))
			remark = "VENTA DE COMBUSTIBLE - PREPAGO: ";
		else
			remark = "VENTA DE COMBUSTIBLE - OTRO: ";
		
		remark += externalProduct+", FECHA: "+issueDateString+", DISPENSADOR: "+dispenser;
		
		Account accountItem = XPersistence.getManager().find(Account.class, accountItemId);
		if (accountItem == null)
			throw new OperativeException("account_item_not_found", accountItemId);
		
		person = JPAHelper.getSingleResult(XPersistence.getManager().createQuery("SELECT o FROM Person o "
				+ "WHERE o.identification = :identification", Person.class)
			.setParameter("identification", identification));
		
		if(person == null) //create person
		{
			person = new Person();
			person.setIdentification(identification);
			
			if (identification.length() == 7)
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "PLA"));
			else if(identification.length() == 10)
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "CED"));
			else if (identification.length() == 13)
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "RUC"));
			else
				person.setIdentificationType(XPersistence.getManager().find(IdentificationType.class, "999"));
		
			if (person.getIdentificationType().getIdentificationTypeId().equals("RUC"))
			{
				person.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.LEGAL_PERSON));
				person.setName(personName);
				person.setExternalCode(personExternalCode);
				XPersistence.getManager().persist(person);
				
				LegalPerson legalPerson = new LegalPerson();
				legalPerson.setPersonId(person.getPersonId());
				legalPerson.setBusinessName(personName);
				legalPerson.setIdentification(identification);
				legalPerson.setIdentificationType(person.getIdentificationType());
				XPersistence.getManager().persist(legalPerson);
			}
			else
			{
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

		System.out.println("Register Sale Invoice Fuel Station No. "+invoiceExternalCode+"... ok!");
	}
	
	private void processAccountingSaleInvoiceFuelStation(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		String remark = "";
		String identification = new String(dataLine[1]);
		String invoiceExternalCode = new String(dataLine[3]);
		String issueDateString = new String(dataLine[4]);
		String hour = new String(dataLine[5]);
		String dispenser = new String(dataLine[10]);
		String externalProduct = new String(dataLine[11]);
		BigDecimal total = new BigDecimal(dataLine[17]);
		String paymentType = new String(dataLine[18]);
		String boxAccountId = new String(dataLine[20]);
		
		Unity unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
		
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
		
		String remarkPayment = "COBRO COMPROBANTE DE VENTA: "+invoiceExternalCode+", COMBUSTIBLE: "+externalProduct+", FECHA: "+issueDateString+" "+hour+", DISPENSADOR: "+dispenser;
		
		 
		Query query = XPersistence.getManager().createQuery("SELECT o FROM Account o, Person p "
				+ "WHERE o.code = :code "
				+ "AND o.accountStatus.accountStatusId = '001' "
				+ "AND o.person.personId = p.personId "
				+ "AND p.identification = :identification")
			.setParameter("code", invoiceExternalCode)
			.setParameter("identification", identification);

		Account account = (Account) JPAHelper.getSingleResult(query);
		
		if (account == null)
			throw new OperativeException("account_not_found", invoiceExternalCode);
		
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		if (accountInvoice == null)
			throw new OperativeException("account_invoice_not_found", invoiceExternalCode);
		
		//AuthorizeTransaction
			
		//InvoiceTransaction
		TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, "INVOICE_SALE");
		transaction.setTransactionModule(tm);
		transaction.setTransactionStatus(tm.getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remark);
		transaction.setDebitAccount(account);
		transaction.setCurrency(account.getCurrency());
		transaction.setOrigenUnity(unity);
		XPersistence.getManager().persist(transaction);
		
		List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoiceSale(transaction, accountingCostOfSale);
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);

		AccountInvoiceHelper.postInvoiceSaleSaveAction(transaction);

		//PaymentInvoiceTransaction
		Account boxAccount = boxAccountId!=null?XPersistence.getManager().find(Account.class, boxAccountId):null;
		if (boxAccount != null)
		{
			TransactionModule tmp = XPersistence.getManager().find(TransactionModule.class, "INVOICESALEPAYMENT");
			transaction = TransactionHelper.getNewInitTransaction();
			transaction.setTransactionModule(tmp);
			transaction.setTransactionStatus(tmp.getFinancialTransactionStatus());
			transaction.setValue(transactionValue);
			transaction.setRemark(remarkPayment);
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
		System.out.println("Post Sale Invoice Fuel Station No. "+invoiceExternalCode);
	}
	
	private void processAccountingPurchaseInvoice(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String accountId = new String(dataLine[0]);
		String code = new String(dataLine[1]);

		Query query = XPersistence.getManager().createQuery("SELECT o FROM Account o "
				+ "WHERE o.accountId = :accountId "
				+ "AND o.accountStatus.accountStatusId = '001' ")
			.setParameter("accountId", accountId);

		Account account = (Account) JPAHelper.getSingleResult(query);
		
		if (account == null)
			throw new OperativeException("account_not_found", code);
		
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		if (accountInvoice == null)
			throw new OperativeException("account_invoice_not_found", code);

		//InvoiceTransaction
		transaction = JPAHelper.getSingleResult(XPersistence.getManager()
				.createQuery("SELECT t FROM Transaction t "
						+ "WHERE t.creditAccount = :account "
						+ "AND t.transactionStatus.transactionStatusId = '001' "
						+ "AND t.transactionModule.transactionModuleId = 'INVOICE_PURCHASE' ", Transaction.class));
		
		if (transaction == null)
			throw new OperativeException("transaction_not_found", account.getAccountId());
		
		List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoicePurchase(transaction);
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);

		AccountInvoiceHelper.postInvoicePurchaseSaveAction(transaction);
		
		System.out.println("Post Purchase Invoice No. "+accountId);
	}
	
	private void processAccountingSaleInvoice(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		String accountId = new String(dataLine[0]);
		String code = new String(dataLine[1]);
		Integer accountingCostOfSale = new Integer(dataLine[2]);

		Query query = XPersistence.getManager().createQuery("SELECT o FROM Account o "
				+ "WHERE o.accountId = :accountId "
				+ "AND o.accountStatus.accountStatusId = '001' ")
			.setParameter("accountId", accountId);

		Account account = (Account) JPAHelper.getSingleResult(query);
		
		if (account == null)
			throw new OperativeException("account_not_found", code);
		
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		if (accountInvoice == null)
			throw new OperativeException("account_invoice_not_found", code);

		//InvoiceTransaction
		query = XPersistence.getManager()
				.createQuery("SELECT t FROM Transaction t "
						+ "WHERE t.debitAccount.accountId = :accountId "
						+ "AND t.transactionStatus.transactionStatusId = '001' "
						+ "AND t.transactionModule.transactionModuleId = 'INVOICE_SALE' ", Transaction.class)
				.setParameter("accountId", accountId);
		
		transaction = (Transaction) JPAHelper.getSingleResult(query);
		
		if (transaction == null)
			throw new OperativeException("transaction_not_found", account.getAccountId());
		
		List<TransactionAccount> transactionAccounts = AccountInvoiceHelper.getTransactionAccountsForInvoiceSale(transaction, accountingCostOfSale);

		transaction.setTransactionStatus(transaction.getTransactionModule().getFinancialTransactionStatus());
		
		XPersistence.getManager().merge(transaction);
		
		TransactionHelper.processTransaction(transaction, transactionAccounts);
		
		AccountInvoiceHelper.postInvoiceSaleSaveAction(transaction);
		
		System.out.println("Post Sale Invoice No. "+accountId);
	}
	
	private void processSaleInvoicePayment(TransactionBatch transactionBatch, TransactionBatchDetail detail, Transaction transaction)
			throws Exception {
		
		String[] dataLine;
		String delimiter = "\t";
		
		dataLine = detail.getDetail().split(delimiter);
		
		validateDataLine(dataLine);
		BigDecimal transactionValue = BigDecimal.ZERO;
		String accountId = new String(dataLine[0]);
		String code = new String(dataLine[1]);

		Unity unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
				
		String remarkPayment = "COBRO COMPROBANTE DE VENTA: "+code;
		
		Query query = XPersistence.getManager().createQuery("SELECT o FROM Account o "
				+ "WHERE o.accountId = :accountId "
				+ "AND o.accountStatus.accountStatusId = '001' ")
			.setParameter("accountId", accountId);

		Account account = (Account) JPAHelper.getSingleResult(query);
		
		if (account == null)
			throw new OperativeException("account_not_found", code);
		
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		if (accountInvoice == null)
			throw new OperativeException("account_invoice_not_found", code);
		

		//PaymentInvoiceTransaction
		transaction.setTransactionModule(transactionBatch.getTransactionModule());
		transaction.setTransactionStatus(transactionBatch.getTransactionModule().getFinancialTransactionStatus());
		transaction.setValue(transactionValue);
		transaction.setRemark(remarkPayment);
		transaction.setCreditAccount(account);
		transaction.setDebitAccount(null);
		transaction.setCurrency(account.getCurrency());
		transaction.setOrigenUnity(unity);
		
		List<TransactionAccount> transactionAccountsPayment = AccountInvoiceHelper.getTAForInvoiceSalePayment(transaction);
		
		if (transactionAccountsPayment != null && !transactionAccountsPayment.isEmpty())
		{
			XPersistence.getManager().persist(transaction);

			TransactionHelper.processTransaction(transaction, transactionAccountsPayment);
			
			AccountInvoiceHelper.postInvoiceSalePaymentSaveAction(transaction);
			
			System.out.println("Sale Invoice Payment No. "+accountId);
		}
		else
		{
			throw new OperativeException("account_invoice_dont_have_payments", accountId);
		}
	}
}
