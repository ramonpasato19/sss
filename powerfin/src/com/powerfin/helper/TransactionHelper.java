package com.powerfin.helper;

import java.math.*;
import java.sql.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class TransactionHelper {

	public final static String TRANSACTION_ANNULLED_STATUS_ID = "003";
	
	public static Account getAccountByPosition(Transaction transaction, int index) throws Exception{
		if (transaction.getTransactionAccounts()==null)
			return null;
		if (transaction.getTransactionAccounts().isEmpty())
			return null;
		return XPersistence.getManager().find(Account.class, ((TransactionAccount)transaction.getTransactionAccounts().get(index)).getAccount().getAccountId());
	}
	
	@SuppressWarnings("rawtypes")
	public static void validateData(String modelName, Map values) throws Exception{
		Messages m = MapFacade.validate(modelName, values);
		if (!m.isEmpty())
			throw new ValidationException(m);		
	}
	
	@SuppressWarnings("rawtypes")
	public static Transaction initTransaction(boolean isKeyEditable, Map keyValues) throws Exception
	{
		Transaction t = new Transaction();
		if(!isKeyEditable)
			t = (Transaction)MapFacade.findEntity("Transaction", keyValues);
		if (t.getTransactionAccounts()==null)
		{
			List<TransactionAccount> list = new ArrayList<TransactionAccount>(); 
			t.setTransactionAccounts(list);
		}
		return t;
	}
	
	public static Transaction getNewInitTransaction() throws Exception
	{
		Transaction t = new Transaction();
		if (t.getTransactionAccounts()==null)
		{
			List<TransactionAccount> list = new ArrayList<TransactionAccount>(); 
			t.setTransactionAccounts(list);
		}
		t.setAccountingDate(CompanyHelper.getCurrentAccountingDate());
		return t;
	}
	
	public static boolean processTransaction(Transaction transaction, 
			List<TransactionAccount> transactionAccounts
			) throws Exception
	{
		if (transactionAccounts == null)
			return processTransaction(transaction);

		List<TransactionAccount> transactionAccountSaved = new ArrayList<TransactionAccount>();
		
		//Clean Transaction Accounts
		XPersistence.getManager().createQuery("DELETE FROM TransactionAccount ta "
					+ "WHERE ta.transaction.transactionId = :transactionId")
				.setParameter("transactionId", transaction.getTransactionId())
				.executeUpdate();
		
		//Save Transaction Accounts
		if (transactionAccounts!=null && !transactionAccounts.isEmpty())
		{
			transaction.setTransactionAccounts(transactionAccounts);
			for (TransactionAccount ta : transactionAccounts)
			{
				ta.setTransaction(transaction);
				XPersistence.getManager().persist(ta);
				transactionAccountSaved.add(ta);
			}
		}
		else
			throw new OperativeException("unable_to_process_transaction_without_detail_of_accouts");
		
		transaction.setTransactionAccounts(transactionAccountSaved);
		
		//Save Financial
		if (isFinancialSaved(transaction))
		{
			FinancialHelper.saveFinancial(transaction);
			return true;
		}
		return false;
	}
	
	public static boolean processTransaction(Transaction transaction) throws Exception
	{		
		//Clean Transaction Accounts with value ZERO
		for (TransactionAccount ta : transaction.getTransactionAccounts())
			if (ta.getValue().compareTo(BigDecimal.ZERO)==0)
				XPersistence.getManager().remove(ta);
		
		//Validate no empty or null Transaction Accounts
		if (transaction.getTransactionAccounts()==null || transaction.getTransactionAccounts().isEmpty())
			throw new OperativeException("unable_to_process_transaction_without_detail_of_accouts");
		
		//Save Financial
		if (isFinancialSaved(transaction))
		{
			FinancialHelper.saveFinancial(transaction);
			return true;
		}
		return false;
	}
	
	public static void saveTransaction(Transaction transaction, 
			boolean isNewTransaction, 
			BigDecimal value, 
			String remark, 
			String transactionSubmoduleName, 
			String transactionStatusId,
			List<TransactionAccount> transactionAccounts) throws Exception
	{
		TransactionStatus transactionStatus = (TransactionStatus)XPersistence.getManager()
				.find(TransactionStatus.class, transactionStatusId);
		
		TransactionModule transactionModule = (TransactionModule)XPersistence.getManager()
				.find(TransactionModule.class, transactionSubmoduleName);
		
		transaction.setTransactionModule(transactionModule);
		transaction.setValue(value);
		transaction.setRemark(remark);
		
		//Set Transaction Status.
		if (transactionStatus!=null)
			transaction.setTransactionStatus(transactionStatus);
		else
			transaction.setTransactionStatus(transactionModule.getDefaultTransactionStatus());
		
		//Set Voucher on create Transaction
		if (isNewTransaction)
			transaction.setVoucher(TransactionHelper.getNewVoucher(transactionModule));
		
		//Create or Update Transaction
		if (isNewTransaction)		
			XPersistence.getManager().persist(transaction);
		else
			XPersistence.getManager().merge(transaction);
		
		//Clean Transaction Accounts
		for (TransactionAccount ta : transaction.getTransactionAccounts())
			XPersistence.getManager().remove(ta);
		
		//Save Transaction Accounts
		for (TransactionAccount ta : transactionAccounts)
		{
			ta.setTransaction(transaction);
			XPersistence.getManager().persist(ta);
		}
		
		//Save Financial
		if (transactionStatus.equals(transactionModule.getFinancialTransactionStatus()))
			FinancialHelper.saveFinancial(transaction);
	}
	
	public static String getNewVoucher(TransactionModule transactionModule) throws Exception
	{
		StringBuilder newCode = new StringBuilder();
		Integer sequence = 1;
		Integer productSequence = TransactionHelper.getSequence(transactionModule);
		if (productSequence!=null)
			sequence = productSequence;
		if(transactionModule.getPrefix()!=null && !transactionModule.getPrefix().trim().isEmpty())
			newCode.append(transactionModule.getPrefix());
		if(transactionModule.getLpad()!=null && !transactionModule.getLpad().trim().isEmpty())
		{
			String lpadCharacter = transactionModule.getLpad().substring(0, 1);
			int lpadLength = transactionModule.getLpad().length();
			int sequenceLength = sequence.toString().length();
			for (int i=0; i<lpadLength-sequenceLength; i++)
			{
				newCode.append(lpadCharacter);
			}
		}
		newCode.append(sequence.toString());
		if(transactionModule.getRpad()!=null && !transactionModule.getRpad().trim().isEmpty())
			newCode.append(transactionModule.getRpad());
		if(transactionModule.getSufix()!=null && !transactionModule.getSufix().trim().isEmpty())
			newCode.append(transactionModule.getSufix());
		return newCode.toString().toUpperCase().trim();
	}

	public static Integer getSequence(TransactionModule transactionModule) throws Exception{
		Integer sequence = null;
		Connection con = null;
		if (transactionModule.getSequenceDBName()!=null)
		{
			try {
				con = DataSourceConnectionProvider.getByComponent("TransactionModule")
						.getConnection();
				Statement stmt = con.createStatement();
				ResultSet res = stmt
						.executeQuery("select nextval('"+CompanyHelper.getSchema().toLowerCase()+"."+transactionModule.getSequenceDBName()+"')");
				if (res.next()) {
					sequence = new Integer(res.getString(1));
				}
				stmt.close();
				return sequence;
			} catch (Exception ex) {
				throw new SystemException(ex);
			} finally {
				try {
					con.close();
				} catch (Exception ex) {
					throw new Exception(ex);
				}
			}
		}
		return sequence;
	}
	
	public static boolean isFinancialSaved(Transaction transaction)
	{
		if (transaction.getTransactionStatus().equals(transaction.getTransactionModule().getFinancialTransactionStatus()))
			return true;
		return false;
	}
	
	public static TransactionStatus getTransactionStatusByStatusId(String transactionStatusId)
	{
		return XPersistence.getManager().find(TransactionStatus.class, transactionStatusId);
	}
}
