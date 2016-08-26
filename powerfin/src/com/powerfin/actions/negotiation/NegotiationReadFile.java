package com.powerfin.actions.negotiation;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class NegotiationReadFile {
	String fileId;
	
	public NegotiationReadFile(String fileId) {
		this.fileId=fileId;
	}
	
	public String execute() throws Exception {
		 byte[] fileBytes = null;
		 
		 File oxfile = XPersistence.getManager().find(File.class, this.fileId);
		 
		 fileBytes = oxfile.getData();
		 String result = new String(fileBytes, "UTF-8");
		 
		 //String hex = result.toString().replace(",", "");
		 //System.out.println("HEX:: "+hex);
		 
		 //String fileString = convertHexToString(hex);
		 
		 //System.out.println("ASCII : " + result);
		 return result;
		 
	}
	
	private String convertHexToString(String hex){

		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
		  
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
			  
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
			  
		      temp.append(decimal);
		  }
		  //System.out.println("DECIMAL : " + dataLine.toString());
		  
		  return sb.toString();
	  }

}
