package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class EmployeeTest extends ModuleTestBase {
	
	EmployeeTest(String testName) {
		super(testName, "Employee")		
	}
	
	void testListWithOneToOneWithPrimaryKeyJoinColumns_alphabeticForSearchingNumericColumnInList() {
		assertValueInList 0, 0, "1" 
		assertValueInList 0, 1, "JUANITO"
		assertValueInList 0, 2, "DEVELOPER"
		assertValueInList 0, 3, "12 YEARS"
		
		setConditionValues (["J"])
		execute "List.filter"
		String html = getHtml()
		assertFalse html.contains("Errors trying to obtain data list")
		assertTrue html.contains("There are 0 records in list")
		assertError "Impossible to filter data: Id must be numeric"
	}
	
}
