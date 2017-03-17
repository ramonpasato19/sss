package org.openxava.test.tests;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

class InvoiceTypicalNewOnInitTest extends ModuleTestBase {
	
	InvoiceTypicalNewOnInitTest(String testName) {
		super(testName, "InvoiceTypicalNewOnInit")		
	}
	
	void testTypicalNewOnInit() {
		assertAction "CRUD.save"
		String currentYear = Integer.toString(Dates.getYear(new Date()))
		assertValue "year", currentYear  
	}
		
}
