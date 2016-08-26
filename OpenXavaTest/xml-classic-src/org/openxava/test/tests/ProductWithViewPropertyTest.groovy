package org.openxava.test.tests

import javax.persistence.*

import org.openxava.tests.*
import org.openxava.util.*

import com.gargoylesoftware.htmlunit.html.*

import static org.openxava.jpa.XPersistence.*


/**
 * @author Javier Paniza
 */

class ProductWithViewPropertyTest extends ModuleTestBase {
	
	ProductWithViewPropertyTest(String testName) {
		super(testName, "ProductWithViewProperty")				
	}

	void testViewPropertyAndValidatorWithFromInTheSameComponent() {
		execute "CRUD.new"
		setValue "number", "66"
		setValue "description", "JUNIT PRODUCT"
		setValue "familyNumber", "1"
		setValue "subfamilyNumber", "1"
		setValue "warehouseKey", "[.1.1.]"
		execute "CRUD.save"				
		assertNoErrors()
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		execute("CRUD.delete");
		assertMessage("Product deleted successfully");
		
	}	
	
	
								
}
