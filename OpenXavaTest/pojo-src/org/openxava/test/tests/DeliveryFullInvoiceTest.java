package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

/**
 *
 * @author Javier Paniza
 */
public class DeliveryFullInvoiceTest extends ModuleTestBase { 
	
	public DeliveryFullInvoiceTest(String testName) {
		super(testName, "DeliveryFullInvoice");		
	}
	
	public void testGeneratePDFFromCollectionOfReferenceInsideSection() throws Exception { 
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_invoice");
		assertCollectionRowCount("invoice.details", 2);
		execute("Print.generatePdf", "viewObject=xava_view_invoice_section1_details");
		assertContentTypeForPopup("application/pdf");
	}
	
}
