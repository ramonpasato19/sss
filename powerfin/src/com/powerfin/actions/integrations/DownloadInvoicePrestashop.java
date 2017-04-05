package com.powerfin.actions.integrations;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.helper.integrations.*;
import com.powerfin.integrations.db.*;

public class DownloadInvoicePrestashop extends SaveAction {
	
	private String sequence;
	
	public void execute() throws Exception {
		
		String host = ParameterHelper.getValue("HOST_PRESTASHOP_"+getSequence());
		String user = ParameterHelper.getValue("USER_PRESTASHOP_"+getSequence());
		String password = ParameterHelper.getValue("PASSWORD_PRESTASHOP_"+getSequence());
		String database = ParameterHelper.getValue("DATABASE_PRESTASHOP_"+getSequence());
		String driverName = ParameterHelper.getValue("DRIVER_NAME_PRESTASHOP_"+getSequence());
		String url = ParameterHelper.getValue("URL_PRESTASHOP_"+getSequence());
		String prefix = ParameterHelper.getValue("PREFIX_PRESTASHOP_"+getSequence());
		
		try {
			 ConnectionManager conection=new ConnectionManager(driverName, url, host, user, password, database);
			 conection.createConnection();
			 PrestashopHelper helper=new PrestashopHelper(prefix);
			 Date fromDate = (Date)getView().getValue("fromDate");
			 Date toDate = (Date)getView().getValue("toDate");
			 int processedInvoices= helper.pullInvoice(conection.getConnection(),fromDate,toDate,getView().getValueString("sequentialCode"));
			 getView().setValue("downloadInvoices", (processedInvoices+""));
			 getView().setValue("notDownloadInvoices", (helper.getNotProcessInvoice()+""));
			 if(helper.getErrors().length()>0)
				 System.out.println("Error Invoice: "+helper.getErrors());
			 conection.commit();
		  } catch (Exception e) {
			 throw new XavaException("no_connection");
		  }
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
}
