package com.powerfin.actions.inventory;

import java.util.*;

import com.powerfin.helper.*;
import com.powerfin.util.report.*;

public class PrintInventoryMinimalStock extends ReportBaseAction{
	
	@SuppressWarnings("rawtypes")
	public Map getParameters() throws Exception {
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		return parameters;
	}
	
	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}
}