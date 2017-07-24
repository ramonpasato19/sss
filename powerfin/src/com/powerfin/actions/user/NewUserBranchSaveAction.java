package com.powerfin.actions.user;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.helper.*;

public class NewUserBranchSaveAction extends SaveAction {

	@SuppressWarnings("unchecked")
	public void execute() throws Exception 
	{
		String userSelected = null;
		String currentUser = UserHelper.getCurrentUserName();
		
		Map<String, String> usersMap = (Map<String, String>) getView().getRoot().getValue("user");
		if (usersMap.get("name")!=null) {
			userSelected = (String)usersMap.get("name");
		}

		super.execute();
		
		if (getErrors().isEmpty()) {
			if (currentUser.equals(userSelected))
				UserHelper.registerBranchInSession(getRequest().getSession());
		}
	}
}
