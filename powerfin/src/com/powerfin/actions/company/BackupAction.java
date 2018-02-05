package com.powerfin.actions.company;

import java.io.*;
import java.text.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.exception.InternalException;
import com.powerfin.helper.*;

public class BackupAction extends SaveAction {

	public void execute() throws Exception {

		SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		String backupScript = ParameterHelper.getValue("BACKUP_SCRIPT");
		String currentAccountingDate = formatDate.format(CompanyHelper.getCurrentAccountingDate());
		String schema = XPersistence.getDefaultSchema().toLowerCase();

		try
		{
			ProcessBuilder pb = new ProcessBuilder(backupScript, schema, currentAccountingDate);
			pb.redirectErrorStream(true);
			Process process = pb.start();
			System.out.println(process.getInputStream().read());
			String output = output(process.getInputStream());
		
			System.out.println(output);
			
			getView().getRoot().setValue("output", output);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new InternalException (ex.getMessage());
		}

		
		
		super.execute();
		
		getView().refresh();
		
		addInfo("backup_completed_please_check_log");
	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

}
