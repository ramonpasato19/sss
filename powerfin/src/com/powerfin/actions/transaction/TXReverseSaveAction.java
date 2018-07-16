package com.powerfin.actions.transaction;

import java.util.ArrayList;
import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.model.Financial;
import com.powerfin.model.Movement;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.model.types.Types;
import com.powerfin.util.UtilApp;

public class TXReverseSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public void extraValidations() throws Exception {
		String voucher = getDocumentNumber();
		if (UtilApp.fieldIsEmpty(voucher))
				throw new OperativeException("document_number_is_required");
		
		List<Financial> financials = XPersistence.getManager().createQuery("SELECT o FROM Financial o "
				+ "WHERE o.voucher=:voucher ")
		.setParameter("voucher", voucher.toUpperCase())
		.getResultList();
		
		if (financials == null || financials.isEmpty())
			throw new OperativeException("transaction_not_found", voucher);
		
		Financial f = financials.get(0);
		
		if (f.getTransaction().getTransactionModule().getAllowsReverseTransaction() == Types.YesNoIntegerType.NO)
			throw new OperativeException("transaction_does_not_allow_reverse", voucher);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		List<Movement> movements = XPersistence.getManager().createQuery("SELECT m FROM Movement m, Financial f "
				+ "WHERE f.voucher = :voucher "
				+ "AND f.financialId = m.financial.financialId")
		.setParameter("voucher", transaction.getDocumentNumber().toUpperCase())
		.getResultList();
		
		for (Movement m : movements)
		{
			TransactionAccount ta = new TransactionAccount();
			ta.setAccount(m.getAccount());
			ta.setBranch(m.getBranch());
			ta.setCategory(m.getCategory());
			ta.setDebitOrCredit(m.getDebitOrCredit().equals(Types.DebitOrCredit.DEBIT)?Types.DebitOrCredit.CREDIT:Types.DebitOrCredit.DEBIT);
			ta.setQuantity(m.getQuantity());
			ta.setRemark(m.getRemark());
			ta.setSubaccount(m.getSubaccount());
			ta.setTransaction(transaction);
			ta.setUnity(m.getUnity());
			ta.setUpdateBalance(Types.YesNoIntegerType.YES);
			ta.setOfficialValue(Types.YesNoIntegerType.NO);
			ta.setValue(m.getValue().abs());
			transactionAccounts.add(ta);
		}
		return transactionAccounts;
	}
}
