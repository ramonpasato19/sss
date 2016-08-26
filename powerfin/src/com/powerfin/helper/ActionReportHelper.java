package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class ActionReportHelper {

	public static String getReportByAction(String actionClass)
	{
		ActionReport ar = XPersistence.getManager().find(ActionReport.class, actionClass.toUpperCase());
		return ar.getReports();
	}
}
