package org.openxava.test.tests;

import java.util.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class AppointmentTest extends ModuleTestBase {
	
	public AppointmentTest(String testName) {
		super(testName, "Appointment");		
	}
	
	public void testDateAsDATETIME() throws Exception {   		
		assertListRowCount(4);
		setConditionValues("5/26/15");
		execute("List.filter");
		assertListRowCount(3);		
		setConditionValues("5/26/15 10:15 AM");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "5/26/15 10:15 AM");
		assertValueInList(0, 1, "ALMUERZO");		
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		excel.nextToken(); // To skip the header
		String line1 = excel.nextToken();
		assertEquals("line1", "\"5/26/15 10:15 AM\";\"ALMUERZO\"", line1);
	}
		
}
