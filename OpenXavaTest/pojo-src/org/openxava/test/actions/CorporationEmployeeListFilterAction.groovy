package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

class CorporationEmployeeListFilterAction extends TabBaseAction {
	
	String segment

	void execute() throws Exception {		
		switch (segment) {			
			case "low":
				print "LOW"
				tab.baseCondition = "e.salary <= 2500"
				break
			case "high": 
				print "HIGH"
				tab.baseCondition = "e.salary > 2500"
				break
			default:
				print "DEFAULT"
				tab.baseCondition = ""
		}				
	}

}
