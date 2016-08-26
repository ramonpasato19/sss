package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class OnChangeVisitKmAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		getView().setValue("description", "VISIT KM " + getNewValue());
		getView().getRoot().setValue("description", "ROUTE KM " + getNewValue());
	}

}
