package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class AccountStatusHelper {

	public static AccountStatus getAccountStatus(String accountStatus)
	{
		AccountStatus status = XPersistence.getManager().find(AccountStatus.class, accountStatus);
		if (status == null)
			throw new InternalException("status_not_found: {0}", accountStatus);
		return status;
	}
}
