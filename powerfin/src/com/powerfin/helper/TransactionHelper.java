package com.powerfin.helper;

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
	public final static String TRANSACTION_REQUEST_STATUS_ID = "001";
	
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
		List<TransactionAccount> transactionAccountSaved = new ArrayList<TransactionAccount>();

		//Clean Old Transaction Accounts
		XPersistence.getManager().createQuery("DELETE FROM TransactionAccount ta "
					+ "WHERE ta.transaction.transactionId = :transactionId")
				.setParameter("transactionId", transaction.getTransactionId())
				.executeUpdate();
		
		//Save New Transaction Accounts
		for (TransactionAccount ta : transactionAccounts)
		{
			ta.setTransaction(transaction);
			XPersistence.getManager().persist(ta);
			transactionAccountSaved.add(ta);
		}
		
		transaction.setTransactionAccounts(transactionAccountSaved);

		return processTransaction(transaction);
		
	}
	
	public static boolean processTransaction(Transaction transaction) throws Exception
	{
		//Save Financial
		if (isFinancialSaved(transaction))
		{
			if (transaction.getTransactionAccounts()==null || transaction.getTransactionAccounts().isEmpty())
				throw new OperativeException("unable_to_process_transaction_without_detail_of_accouts");
			
			FinancialHelper.saveFinancial(transaction);
			return true;
		}
		return false;
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
	
	public static boolean isRequest(Transaction transaction)
	{
		if (transaction.getTransactionStatus().getTransactionStatusId().equals(TransactionHelper.TRANSACTION_REQUEST_STATUS_ID))
			return true;
		return false;
	}
	
	public static TransactionStatus getTransactionStatusByStatusId(String transactionStatusId)
	{
		return XPersistence.getManager().find(TransactionStatus.class, transactionStatusId);
	}
}
