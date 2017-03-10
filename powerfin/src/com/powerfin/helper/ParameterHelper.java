package com.powerfin.helper;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;

public class ParameterHelper {

	public final static String CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID = "CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID";
	public final static String CREDIT_NOTE_SALE_PRODUCT_TYPE_ID = "CREDIT_NOTE_SALE_PRODUCT_TYPE_ID";

	public static String getValue(String parameterId) throws Exception {
		Parameter p = XPersistence.getManager().find(Parameter.class, parameterId);

		if (p == null)
			throw new OperativeException("parameter_not_found", parameterId);

		return p.getValue();

	}

}
