package com.powerfin.actions.person;

import java.util.*;

import net.sf.jasperreports.engine.*;

import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.report.*;

public class PrintNaturalPersonAction extends ReportBaseAction {

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {

		Messages errors = MapFacade.validate("Person", getView().getValues());
		if (errors.contains())
			throw new ValidationException(errors);
		Map parameters = new HashMap();
		Integer id = (Integer) getView().getRoot().getValueInt("personId");
		Person object = XPersistence.getManager().find(Person.class, id);
		
		if (object != null) {
			addDefaultParameters(parameters);
			parameters.put("PERSON_ID", object.getPersonId());
		} else{
			throw new OperativeException("no_record_to_print");
		}

		return parameters;
	}

	@Override
	protected JRDataSource getDataSource() throws Exception {
		return null;
	}

	@Override
	protected String getJRXML() throws Exception {
		return null;
	}

	@Override
	protected String getReportName() throws Exception {
		return ActionReportHelper.getReportByAction(this.getClass().getName());
	}

}
