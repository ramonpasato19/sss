package com.powerfin.actions.company;

import java.io.File;

import org.openxava.actions.SaveAction;

public class ShowLogAction extends SaveAction {

	public void execute() throws Exception {

		Integer lines = (Integer) getView().getRoot().getValue("lines");
		if (lines == null)
		{
			lines = 1000;
			getView().getRoot().setValue("lines", lines);
		}

		File catalinaOut = new File(System.getProperty("catalina.base"), "logs/catalina.out");
		
		if (catalinaOut.exists()) {		
			getView().getRoot().setValue("output", getLastNLogLines(catalinaOut, lines));
		}

		super.execute();

		getView().refresh();

		addInfo("log_file_displayed");
	}

	public String getLastNLogLines(File file, int nLines) {
	    StringBuilder s = new StringBuilder();
	    try {
	        Process p = Runtime.getRuntime().exec("tail -"+nLines+" "+file);
	        java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
	        String line = null;
	    //Here we first read the next line into the variable
	    //line and then check for the EOF condition, which
	    //is the return value of null
	    while((line = input.readLine()) != null){
	            s.append(line+'\n');
	        }
	    } catch (java.io.IOException e) {
	        e.printStackTrace();
	    }
	    return s.toString();
	}
	

}
