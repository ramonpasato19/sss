package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class ActionReportHelper {

	public static String getReportByAction(String actionClass) throws Exception
	{
		ActionReport ar = XPersistence.getManager().find(ActionReport.class, actionClass.toUpperCase());
		if (ar==null)
			throw new InternalException("action_report_not_found: "+actionClass.toUpperCase());
		return ar.getReports();
	}
}
