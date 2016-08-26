package org.openxava.validators.hibernate;

import javax.validation.*; 
import org.openxava.annotations.*;
import org.openxava.annotations.parse.*;
import org.openxava.validators.*;
import org.openxava.validators.meta.*;

/**
 * Implements a PropertyValidator of OpenXava as a Hibernate validator. <p>
 *  
 * @author Javier Paniza
 */

public class PropertyValidatorValidator implements ConstraintValidator<PropertyValidator, Object> {
		
	private MetaValidator metaValidator;

	public void initialize(PropertyValidator propertyValidator) {
		metaValidator = AnnotatedClassParser.createPropertyValidator(propertyValidator);
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) { 	
		if (HibernateValidatorInhibitor.isInhibited()) return true;  // Usually when saving from MapFacade, MapFacade already has done the validation
		if (metaValidator.isOnlyOnCreate()) return true;
		try {			
			IPropertyValidator v = metaValidator.getPropertyValidator();			
			v.validate(FailingMessages.getInstance(), value, "", "");
			return true;
		}
		catch (IllegalStateException ex) {
			if (FailingMessages.EXCEPTION_MESSAGE.equals(ex.getMessage())) return false;
			throw ex;
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}		
	}

}
