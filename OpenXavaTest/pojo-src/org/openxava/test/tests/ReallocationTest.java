package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class ReallocationTest extends ModuleTestBase {
	
	public ReallocationTest(String testName) {
		super(testName, "Reallocation");		
	}	
	
	public void testDefaultValueCalculatorForReferencesInElementCollections() throws Exception {
		execute("Mode.detailAndFirst");
		assertCollectionRowCount("details", 3);
		setValueInCollection("details", 3, "place", "4MATIC");
		assertValueInCollection("details", 3, "product.number", "1");
		assertValueInCollection("details", 3, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 3, "product.unitPrice", "11.00");

		execute("CRUD.new");
		assertNoErrors();
		assertCollectionRowCount("details", 0);
		setValueInCollection("details", 0, "place", "THE PLACE");
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "product.unitPrice", "11.00");		
		
		setValueInCollection("details", 1, "place", "THE SECOND PLACE");
		assertValueInCollection("details", 1, "product.number", "1");
		assertValueInCollection("details", 1, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 1, "product.unitPrice", "11.00");		
		setValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		assertValueInCollection("details", 1, "product.unitPrice", "20.00");
		
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "product.unitPrice", "11.00");
		
		setValueInCollection("details", 2, "place", "THE THIRD PLACE");
		assertValueInCollection("details", 2, "product.number", "1");
		assertValueInCollection("details", 2, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 2, "product.unitPrice", "11.00");		
	}
	
}
