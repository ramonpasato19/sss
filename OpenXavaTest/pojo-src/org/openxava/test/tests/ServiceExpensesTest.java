package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class ServiceExpensesTest extends ModuleTestBase {
	
	public ServiceExpensesTest(String testName) {
		super(testName, "ServiceExpenses");		
	}
		
	public void testSeachKeysEnumsAndDescriptionsListInElementCollection() throws Exception { 
		execute("CRUD.new");
		assertLabelInCollection("expenses", 4, "Receptionist");
		setValue("description", "JUNIT EXPENSES");
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		assertValueInCollection("expenses", 0, "invoice.amount", "");
		setValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		
		String [][] statusValidValues = {
			{ "", "" },
			{ "0", "Paid" },
			{ "1", "Pending" },
			{ "2", "Rejected" }
		};		
		assertValidValuesInCollection("expenses", 0, "status", statusValidValues);
		setValueInCollection("expenses", 0, "status", "1");
		
		String [][] receptionistValidValues = {
			{ "", "" },
			{ "2", "ANTONIOA" },
			{ "3", "JUAN" },
			{ "1", "PEPE" },
			{ "4", "PEPE" }
		};
		assertValidValuesInCollection("expenses", 0, "receptionist.oid", receptionistValidValues);
		setValueInCollection("expenses", 0, "receptionist.oid", "3");		
		
		assertValueInCollection("expenses", 1, "invoice.year", "");
		assertValueInCollection("expenses", 1, "invoice.number", "");
		assertValueInCollection("expenses", 1, "invoice.amount", "");
		execute("Reference.search", "keyProperty=expenses.1.invoice.number");
		execute("ReferenceSearch.choose", "row=0");
		assertValueInCollection("expenses", 1, "invoice.year", "2007");
		assertValueInCollection("expenses", 1, "invoice.number", "1");
		assertValueInCollection("expenses", 1, "invoice.amount", "790.00");

		setValueInCollection("expenses", 1, "status", "2");
		setValueInCollection("expenses", 1, "receptionist.oid", "4");		
		
		execute("CRUD.save");
		
		assertValue("description", "");
		assertValueInCollection("expenses", 0, "invoice.year", "");
		assertValueInCollection("expenses", 0, "invoice.number", "");
		assertValueInCollection("expenses", 0, "invoice.amount", "");
		assertValueInCollection("expenses", 0, "status", "");
		assertValueInCollection("expenses", 0, "receptionist.oid", "");
		assertValueInCollection("expenses", 1, "invoice.year", "");
		assertValueInCollection("expenses", 1, "invoice.number", "");
		assertValueInCollection("expenses", 1, "invoice.amount", "");
		assertValueInCollection("expenses", 1, "status", "");
		assertValueInCollection("expenses", 1, "receptionist.oid", "");		
		
		execute("Mode.list");
		execute("Mode.detailAndFirst");

		assertValue("description", "JUNIT EXPENSES");
		assertValueInCollection("expenses", 0, "invoice.year", "2007");
		assertValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		assertValueInCollection("expenses", 0, "status", "1");
		assertValueInCollection("expenses", 0, "receptionist.oid", "3");
		assertValueInCollection("expenses", 1, "invoice.year", "2007");
		assertValueInCollection("expenses", 1, "invoice.number", "1");
		assertValueInCollection("expenses", 1, "invoice.amount", "790.00");
		assertValueInCollection("expenses", 1, "status", "2");
		assertValueInCollection("expenses", 1, "receptionist.oid", "4");		
		
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
