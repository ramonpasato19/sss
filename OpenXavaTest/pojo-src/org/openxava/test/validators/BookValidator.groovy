package org.openxava.test.validators

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */

class BookValidator implements IValidator, IWithMessage {
	
	private String message
	String title
	String synopsis

	void validate(Messages errors) {
		if (title.contains("EL QUIJOTE") && synopsis.contains("JAVA")) {
			errors.add message
		}		
	}	
	void setMessage(String message) {
		this.message = message;		
	}

}
