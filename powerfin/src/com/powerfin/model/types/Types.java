package com.powerfin.model.types;

public class Types {

	public enum YesNoIntegerType {
		NO, YES
	};
	public enum DebitOrCredit {
		DEBIT,CREDIT
	}
	public enum StartEnd {
		START,END
	}
	public enum RateValue {
		RATE,VALUE
	}
	public enum CommonStatus {
		REQ ("REQUESTED"),
		AUT ("AUTHORIZED"),
		CAN ("CANCELLED");

	    private final String name;       

	    private CommonStatus(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }

	};
	
	public enum TaxBaseType {
		NOTTAX ("NOTTAX"),
		ZERTAX ("ZERTAX"),
		XXXTAX ("XXXTAX");
		
		
		private final String name;       

	    private TaxBaseType(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	};
}
