package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailsWithSectionsTest extends ModuleTestBase {
	
	public InvoiceDetailsWithSectionsTest(String testName) {
		super(testName, "InvoiceDetailsWithSections");		
	}
	
	public void testFocusInDialogWithAllMembersInSections() throws Exception {  
		execute("Mode.detailAndFirst");
		execute("Invoice.editDetailWithSections", "row=0,viewObject=xava_view_details");
		assertFocusOn("serviceType");
	}	
				
}