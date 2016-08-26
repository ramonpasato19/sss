package org.openxava.test.actions

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
class FilterInvoice4ByDateAction extends TabBaseAction {

	void execute() {
		Date date = Dates.create 4, 1, 2004
		tab.setConditionValue "date", date 
	}

}
