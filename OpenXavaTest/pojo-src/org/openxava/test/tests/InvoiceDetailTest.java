package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * Testing InvoiceDetail as a module is somewhat unorthodox, 
 * but practical for testing some case. 
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailTest extends ModuleTestBase {
	
	public InvoiceDetailTest(String testName) {
		super(testName, "InvoiceDetail"); 		
	}
	
	public void testImagesGalleryInsideAReference() throws Exception {  
		execute("CRUD.new");
		execute("Gallery.edit", "galleryProperty=photos,viewObject=xava_view_product");
		assertNoErrors();
		assertMessage("No images");
		assertDialog();
		execute("Close.close"); 
		assertNoDialog();
		assertExists("quantity");
	}
		
}
