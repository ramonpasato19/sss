package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

class Invoice4Test extends ModuleTestBase {
	
	Invoice4Test(String testName) {
		super(testName, "Invoice4")		
	}
	
	void testHibernateTypeDef() {
		assertValueInList 0, 0, "2002"
		assertValueInList 0, 1, "1"
		assertValueInList 0, 3, "No"
		assertValueInList 1, 0, "2004"
		assertValueInList 1, 1, "2"
		assertValueInList 1, 3, "Yes"
		
		execute "Mode.detailAndFirst"
		assertValue "year", "2002"
		assertValue "number", "1"
		assertValue "paid", "false"		
		execute "Navigation.next"
		assertValue "year", "2004"
		assertValue "number", "2"
		assertValue "paid", "true"
	}
	
	void testTabSetConditionValueByDate() {
		execute "Invoice4.filterByDate"
		assertListRowCount 2
		assertValueInList 0, 2, "1/4/04"
		assertValueInList 1, 2, "1/4/04"
	}
	
}
