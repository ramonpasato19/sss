package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class JavaProgrammerWithGroupInSectionTest extends ModuleTestBase {
	
	public JavaProgrammerWithGroupInSectionTest(String testName) {
		super(testName, "JavaProgrammerWithGroupInSection");		
	}
		
	public void testViewInheritanceWithGroupInsideSection() throws Exception { 
		assertNoErrors();
		execute("Mode.detailAndFirst");
		assertCollectionRowCount("experiences", 0);
		assertExists("sex");
		assertValue("mainLanguage", "JAVA");
	}
			
}
