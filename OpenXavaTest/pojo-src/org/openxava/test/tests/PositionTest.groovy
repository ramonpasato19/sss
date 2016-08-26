package org.openxava.test.tests

import org.openxava.tests.ModuleTestBase 

/**
 * 
 * @author Javier Paniza
 */
class PositionTest extends ModuleTestBase {
	
	PositionTest(String testName) {
		super(testName, "Position")		
	}
	
	void testFloatDoublePrecision() { 		
		execute "CRUD.new"
		setValue "name", "JUNIT POSITION"
		setValue "axisX", "1234.1234"
		setValue "axisY", "1234567.1234567"
		execute "CRUD.save"
		execute "Mode.list"
		assertValueInList 0, 0, "JUNIT POSITION"
		assertValueInList 0, 1, "1,234.1234"
		assertValueInList 0, 2, "1,234,567.1234567"
		execute "Mode.detailAndFirst"
		assertValue "name", "JUNIT POSITION"
		assertValue "axisX", "1,234.1234"
		assertValue "axisY", "1,234,567.1234567"
		execute "CRUD.delete"
		assertNoErrors()
	}
	
}
