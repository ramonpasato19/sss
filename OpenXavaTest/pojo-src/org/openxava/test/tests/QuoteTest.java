package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;



/**
 * @author Javier Paniza
 */

public class QuoteTest extends ModuleTestBase {
	
	public QuoteTest(String testName) {
		super(testName, "Quote");		
	}
	
	public void testElementCollection() throws Exception {
		execute("CRUD.new");		
		setValue("year", "2015");
		setValue("number", "66");
		setValue("date", "1/1/15");
		setValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		
		assertCollectionRowCount("details", 0);
		execute("CRUD.save");
		assertError("It's required at least 1 element in Details of Quote");
		assertErrorsCount(1);
		
		assertEditableInCollection("details", 0, 0);
		assertNoEditableInCollection("details", 0, 1);
		assertEditableInCollection("details", 0, "unitPrice");
		assertEditableInCollection("details", 0, "quantity");
		assertNoEditableInCollection("details", 0, "amount");

		setValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "unitPrice", "11.00");		
		setValueInCollection("details", 0, "unitPrice", "100");
		setValueInCollection("details", 0, "quantity", "2");
		assertValueInCollection("details", 0, "amount", "200.00");
		
		assertTotalInCollection("details", 0, "amount", "200.00");
		assertTotalInCollection("details", 1, "amount",  "42.00");
		assertTotalInCollection("details", 2, "amount", "242.00");
		
		execute("Reference.search", "keyProperty=details.1.product.number");
		execute("ReferenceSearch.choose", "row=1");
		assertValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		assertValueInCollection("details", 1, "unitPrice", "20.00");		
		setValueInCollection("details", 1, "unitPrice", "7000");
		setValueInCollection("details", 1, "quantity", "1");
		assertValueInCollection("details", 1, "amount", "7,000.00");
		
		assertTotalInCollection("details", 0, "amount", "7,200.00");
		assertTotalInCollection("details", 1, "amount", "1,512.00");
		assertTotalInCollection("details", 2, "amount", "8,712.00");
		
		assertCollectionRowCount("details", 2);		
		execute("CRUD.save");
		assertValue("year", "");
		assertValue("number", "");
		assertValue("customer.number", "");		
		assertCollectionRowCount("details", 0);
		
		execute("Mode.list");
		execute("Mode.detailAndFirst");		
		
		assertValue("year", "2015");
		assertValue("number", "66");
		assertValue("date", "1/1/15");
		assertValue("customer.number", "1");
		
		assertCollectionRowCount("details", 2);

		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "unitPrice", "100.00");
		assertValueInCollection("details", 0, "quantity", "2");
		assertValueInCollection("details", 0, "amount", "200.00");		
		assertValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		assertValueInCollection("details", 1, "unitPrice", "7,000.00");
		assertValueInCollection("details", 1, "quantity", "1");
		assertValueInCollection("details", 1, "amount", "7,000.00");
		
		assertTotalInCollection("details", 0, "amount", "7,200.00");
		assertTotalInCollection("details", 1, "amount", "1,512.00");
		assertTotalInCollection("details", 2, "amount", "8,712.00");
		
		setValueInCollection("details", 1, "quantity", "2");
		assertValueInCollection("details", 1, "amount", "14,000.00");
		
		assertTotalInCollection("details", 0, "amount", "14,200.00");
		assertTotalInCollection("details", 1, "amount",  "2,982.00");
		assertTotalInCollection("details", 2, "amount", "17,182.00");
		
		execute("Reference.search", "keyProperty=details.0.product.number");
		execute("ReferenceSearch.choose", "row=3");
		assertValueInCollection("details", 0, "product.number", "4");		
		assertValueInCollection("details", 0, "product.description", "CUATRE");
		
		setValueInCollection("details", 2, "product.number", "3");
		setValueInCollection("details", 2, "unitPrice", "300.00");
		setValueInCollection("details", 2, "quantity", "3");
		setValueInCollection("details", 3, "product.number", "3");
		setValueInCollection("details", 3, "unitPrice", "300.00");
		setValueInCollection("details", 3, "quantity", "3");
		execute("CRUD.save");
		assertError("More than 3 items in Details of Quote are not allowed");		
		assertErrorsCount(1);
		
		execute("CRUD.new");
		setValueInCollection("details", 0, "unitPrice", "100");
		setValueInCollection("details", 0, "quantity", "2");
		assertValueInCollection("details", 0, "amount", "200.00");
		
		assertTotalInCollection("details", 0, "amount", "200.00");
		assertTotalInCollection("details", 1, "amount",  "42.00");
		assertTotalInCollection("details", 2, "amount", "242.00");
		
		execute("Mode.list");
		execute("Mode.detailAndFirst");
		assertValue("year", "2015");
		assertValue("number", "66");		
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testDependentDefaultValueCalculatorInElementCollection() throws Exception { 
		execute("CRUD.new");		

		setValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "unitPrice", "11.00");		
		setValueInCollection("details", 0, "quantity", "2");
		assertValueInCollection("details", 0, "amount", "22.00");
		
		assertTotalInCollection("details", 0, "amount", "22.00");
		assertTotalInCollection("details", 1, "amount",  "4.62");
		assertTotalInCollection("details", 2, "amount", "26.62");
		
		execute("Reference.search", "keyProperty=details.1.product.number");
		execute("ReferenceSearch.choose", "row=1");
		assertValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "unitPrice", "20.00");		
		setValueInCollection("details", 1, "quantity", "3");
		assertValueInCollection("details", 1, "amount", "60.00");
		
		assertTotalInCollection("details", 0, "amount", "82.00");
		assertTotalInCollection("details", 1, "amount", "17.22");
		assertTotalInCollection("details", 2, "amount", "99.22");

		
		setValueInCollection("details", 0, "product.number", "2");
		assertValueInCollection("details", 0, "unitPrice", "20.00");		
		assertValueInCollection("details", 0, "amount", "40.00");
		
		assertTotalInCollection("details", 0, "amount", "100.00");
		assertTotalInCollection("details", 1, "amount",  "21.00");
		assertTotalInCollection("details", 2, "amount", "121.00");
		
		execute("Reference.search", "keyProperty=details.1.product.number");
		execute("ReferenceSearch.choose", "row=0");
		assertValueInCollection("details", 1, "product.number", "1");
		assertValueInCollection("details", 1, "unitPrice", "11.00");		
		assertValueInCollection("details", 1, "amount", "33.00");
		
		assertTotalInCollection("details", 0, "amount", "73.00");
		assertTotalInCollection("details", 1, "amount", "15.33");
		assertTotalInCollection("details", 2, "amount", "88.33");		
	}
	
	public void testRemovingRowUpdatesTotals() throws Exception {  
		execute("Mode.detailAndFirst");
		assertValue("year", "2014"); // This one ...
		assertValue("number", "1");  // ... has 3 details
		assertTotalInCollection("details", 0, "amount", "162.00");
		assertTotalInCollection("details", 1, "amount",  "34.02");
		assertTotalInCollection("details", 2, "amount", "196.02");
		
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Quote__details___1"); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("img").get(0);
		removeIcon.click();		
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		
		assertTotalInCollection("details", 0, "amount", "102.00");
		assertTotalInCollection("details", 1, "amount",  "21.42");
		assertTotalInCollection("details", 2, "amount", "123.42");
		
		
		setValueInCollection("details", 1, "quantity", "5");
		assertValueInCollection("details", 1, "amount", "100.00");
		assertTotalInCollection("details", 0, "amount", "122.00");
		assertTotalInCollection("details", 1, "amount",  "25.62");
		assertTotalInCollection("details", 2, "amount", "147.62");
	}
		
}
