package com.powerfin.actions.balanceAccounting;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.*;

public class BalanceAccountingGenerateAction extends ViewBaseAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		Date accountingDate = (Date)getView().getValue("accountingDate");
		if (accountingDate==null)
			accountingDate = CompanyHelper.getCurrentAccountingDate();
		
		getView().setValue("accountingDate",accountingDate);
		
		BalanceAccountingHelper.generateBalanceSheet(accountingDate);
		getView().refreshCollections();
		List<BalanceAccounting> balances = XPersistence.getManager().createQuery("select o FROM BalanceAccounting o "
				+ "where o.accountingDate = :accountingDate and o.bookAccount.level = 1")
				.setParameter("accountingDate", accountingDate)
				.getResultList();
		
		BigDecimal assetsValue = BigDecimal.ZERO;
		BigDecimal liabilitiesValue = BigDecimal.ZERO;
		BigDecimal capitalValue = BigDecimal.ZERO;
		BigDecimal incomeValue = BigDecimal.ZERO;
		BigDecimal expenseValue = BigDecimal.ZERO;
		
		for (BalanceAccounting ba:balances)
		{
			if (ba.getBookAccount().getLevel()==1)
			{
				if (ba.getOfficialBalance()!=null)
				{
					if (ba.getBookAccount().getGroupAccount().getGroupAccountId().equals("1"))
						assetsValue = assetsValue.add(ba.getOfficialBalance());
					else if(ba.getBookAccount().getGroupAccount().getGroupAccountId().equals("2"))
						liabilitiesValue = liabilitiesValue.add(ba.getOfficialBalance());
					else if(ba.getBookAccount().getGroupAccount().getGroupAccountId().equals("3"))
						capitalValue = capitalValue.add(ba.getOfficialBalance());
					else if(ba.getBookAccount().getGroupAccount().getGroupAccountId().equals("4"))
						incomeValue = incomeValue.add(ba.getOfficialBalance());
					else if(ba.getBookAccount().getGroupAccount().getGroupAccountId().equals("5"))
						expenseValue = expenseValue.add(ba.getOfficialBalance());
				}
			}
		}
		getView().setValue("assetsValue",assetsValue);
		getView().setValue("liabilitiesAndCapital",liabilitiesValue.add(capitalValue).add(incomeValue).subtract(expenseValue));
		
		addMessage("generated_balance_sheet_date", UtilApp.dateToString(accountingDate));
	}

}
