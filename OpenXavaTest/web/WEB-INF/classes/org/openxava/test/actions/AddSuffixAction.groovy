package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class AddSuffixAction extends ViewBaseAction {
	
	String property
	String suffix
	
	void execute() {
		String value = view.getValue(property)
		view.setValue(property, value.toLowerCase() + suffix)
	}

}
