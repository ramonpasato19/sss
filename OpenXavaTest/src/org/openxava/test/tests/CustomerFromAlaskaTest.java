package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */
public class CustomerFromAlaskaTest extends ImageTestBase { 	
	
	public CustomerFromAlaskaTest(String testName) {
		super(testName, "CustomerFromAlaska");
	}
				
	public void testBaseConditionStartedByFrom() throws Exception { 
		assertListRowCount(1);
		assertValueInList(0, "number", "43");
		assertValueInList(0, "name", "Gonzalo Gonzalez");
	}
			
}
