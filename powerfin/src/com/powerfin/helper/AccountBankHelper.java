package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class AccountBankHelper {

	public static final String CHECK_EMITTED="EMI";
	public static final String CHECK_EFFECTIVE="EFF";
	
	public static void validateCheckNumber(Account accountBank, String checkNumber) throws Exception
	{
		Integer checkNumberInt;
		if (accountBank==null)
			throw new InternalException("null_account_bank_to_validate_check_number");
		
		if (checkNumber==null)
			throw new InternalException("null_checkNumber_to_validate");
		
		try
		{
			checkNumberInt=Integer.parseInt(checkNumber);
		}catch (Exception e)
		{
			throw new OperativeException("invalid_check_number", checkNumber);
		}
		
		AccountBankCheckPK checkPk = new AccountBankCheckPK();
		checkPk.setAccountId(accountBank.getAccountId());
		checkPk.setCheckNumber(checkNumberInt);
				
		if (XPersistence.getManager().find(AccountBankCheck.class, checkPk)!=null) 
			throw new OperativeException("check_number_is_already_used", checkNumber);
	}
	
	public static boolean validateAndEmitCheck(Account accountBank, String checkNumber, String detail) throws Exception
	{
		validateCheckNumber(accountBank, checkNumber);
		return emitCheck(accountBank, checkNumber, detail);
	}
	public static boolean emitCheck(Account accountBank, String checkNumber, String detail) throws Exception
	{
		AccountBankCheckPK checkPk = new AccountBankCheckPK();
		checkPk.setAccountId(accountBank.getAccountId());
		checkPk.setCheckNumber(Integer.parseInt(checkNumber));
		AccountBankCheck check = new AccountBankCheck();
		check.setId(checkPk);
		check.setDetail(detail);
		check.setStatusCheck(AccountBankHelper.CHECK_EMITTED);
		XPersistence.getManager().persist(check);
		return true;
	}
}
