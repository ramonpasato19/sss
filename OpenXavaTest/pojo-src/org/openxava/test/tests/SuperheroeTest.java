package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class SuperheroeTest extends ModuleTestBase {
	
	public SuperheroeTest(String testName) {
		super(testName, "Superheroe");		
	}
	
	public void testHTMLFilterListFormatter() throws Exception {
		execute("CRUD.new");
		setValue("name", "<b>Super</b>man");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, "<b>Super</b>man"); 
		execute("CRUD.deleteRow", "row=0");
	}
	
}
