package org.openxava.test.tests;

import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * Create on 27/02/2012 (10:56:25)
 * @author Ana Andres
 * @author Javier Paniza
 */
public class CityTest extends ModuleTestBase{

	public CityTest(String testName) {
		super(testName, "City");
	}
	
	public void testStateFullNameWithFormulaFromAReference_listFormatter_isolateModuleSessionForEachBrowserTab() throws Exception {
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "PHOENIX CITY"); 
		assertLabelInList(2, "Full name with formula of State");
		assertValueInList(0, 2, "AZARIZONA");
		execute("Mode.detailAndFirst");
		assertValue("name", "Phoenix");
		
		reload();
		assertValue("name", "Phoenix");
		
		getHtmlPage().executeJavaScript("window.open(window.location, '_blank')");
		HtmlPage newTabPage = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		String tabText = newTabPage.asText();
		assertTrue(tabText.contains("New Generate PDF Generate Excel")); // In list mode
		assertFalse(tabText.contains("New Save Delete Search Refresh")); // Not in detail mode		
	}
		
}
