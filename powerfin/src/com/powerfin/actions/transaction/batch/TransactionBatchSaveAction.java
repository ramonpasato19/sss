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
			
			transactionBatch.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthInProcessStatus());
			XPersistence.getManager().merge(transactionBatch);
			
			String transactionModuleId = transactionBatch.getTransactionModule().getTransactionModuleId();

			List<TransactionBatchDetail> details = XPersistence.getManager().createQuery("SELECT tbd FROM TransactionBatchDetail tbd "
					+ "WHERE transactionBatch.transactionBatchId = :transactionBatchId "
					+ "AND transactionBatchStatus.transactionBatchStatusId = :transactionBatchStatusId "
					+ "ORDER by line")
			.setParameter("transactionBatchId", transactionBatch.getTransactionBatchId())
			.setParameter("transactionBatchStatusId", TransactionBatchHelper.TRANSACTION_BATCH_DETAIL_CREATE_STATUS)
			.getResultList();
			
			for (TransactionBatchDetail detailIte : details) {
				TransactionBatchDetail detail = XPersistence.getManager().find(TransactionBatchDetail.class, detailIte.getTransactionBatchDetailId());
				try {
					
					if (transactionModuleId.equals("PURCHASEPORTFOLIOPAYMENT"))
						processPurchasePortfolioPayment(transactionBatch, detail);
					else if (transactionModuleId.equals("SALEPORTFOLIOPAYMENT"))
						processSalePortfolioPayment(transactionBatch, detail);
					else
						throw new OperativeException("transaction_module_not_found_for_batch_process");

					detail.setTransactionBatchStatus(TransactionBatchHelper.getTransactionBacthDetailProcessStatus());
					XPersistence.getManager().merge(detail);

				} catch (Exception e) {
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
}
