package org.openxava.test.tests

import org.openxava.tests.*;

class MiniOrderTest extends ModuleTestBase { 
	
	MiniOrderTest(String testName) {
		super(testName, "MiniOrder")		
	}
	
	void testAutomatedBusinessLogic() { 
		execute "CRUD.new"
		setValue "number", "66"		
		setValue "description", "ORDER JUNIT"
		setValue "productPrice", "2"
		setValue "qtyOrdered", "3"
		execute "CRUD.save"
		assertNoErrors()
		assertValue "number", ""
		setValue "number", "66"
		execute "CRUD.refresh"
		assertValue "amount", "6.00" 
		
		// Delete
		execute "CRUD.delete"
		assertNoErrors()
		assertMessage "Mini order deleted successfully"
	}
				
}
