package org.openxava.test.validators

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */
class BookTitleValidator implements IPropertyValidator, IWithMessage {
	
	private String message;

	void setMessage(String message) {
		this.message = message		
	}

	void validate(Messages errors, Object value, String propertyName, String modelName) {
		if (((String)value).contains("RPG")) {
			errors.add message
		}		
	}

}
