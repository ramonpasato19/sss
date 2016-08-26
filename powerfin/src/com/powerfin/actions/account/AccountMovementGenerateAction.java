package com.powerfin.actions.account;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;

public class AccountMovementGenerateAction extends ViewBaseAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");
		String accountId = getView().getSubview("account").getValueString("accountId");
		//String categoryId = getView().getSubview("category").getValueString("categoryId");
		Map<String, String> mapCategories = (Map<String, String>) getView().getValue("category");
		String categoryId = (String)mapCategories.get("categoryId");
		
		if (accountId==null)
			throw new OperativeException("account_is_required");
		
		if (categoryId==null)
			throw new OperativeException("category_is_required");
		
		if (fromDate==null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		
		if (toDate==null)
			toDate = CompanyHelper.getCurrentAccountingDate();
		
		getView().setValue("fromDate",fromDate);
		getView().setValue("toDate",toDate);
				
		getView().refreshCollections();
	}

}
