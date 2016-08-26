package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CorporationEmployeeTest extends ModuleTestBase {
	
	CorporationEmployeeTest(String testName) {
		super(testName, "CorporationEmployee")		
	}
	
	void testSimpleHTMLReport() {
		execute "Mode.detailAndFirst"
		execute "CorporationEmployee.report"
		assertNoError()
		assertTrue getPopupText().contains("<tr><td>Corporation:</td><td>RANONE</td></tr>")
	}
	
	void testTabEditorForModel() {
		assertListRowCount 2
		setValue "chooseSegment", "low"
		assertListRowCount 1
		assertValueInList 0, 0, "MIGUEL"
		setValue "chooseSegment", "high"
		assertListRowCount 1
		assertValueInList 0, 0, "ANA"
	}
	
}
