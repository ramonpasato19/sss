package org.openxava.test.tests;

import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class CustomerSellerAsDescriptionsListShowingReferenceViewNoKeyTest extends ModuleTestBase {
	
	public CustomerSellerAsDescriptionsListShowingReferenceViewNoKeyTest(String testName) {
		super(testName, "CustomerSellerAsDescriptionsListShowingReferenceViewNoKey");				
	}
	
	public void testDescriptionsListWithShowReferenceViewNoKey() throws Exception { 
		execute("Mode.detailAndFirst");
		assertValue("name", "Javi");
		assertValue("seller.name", "MANUEL CHAVARRI");
		assertNoEditable("seller.name");
		assertNoEditable("seller.level");
		HtmlElement sellerEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_CustomerSellerAsDescriptionsListShowingReferenceViewNoKey__reference_editor_seller");
		HtmlElement openSellerListIcon = sellerEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		openSellerListIcon.click();
		HtmlElement menuItem = (HtmlElement) getHtmlPage().getElementById("ui-id-3");
		assertEquals("ui-menu-item", menuItem.getAttribute("class"));
		assertEquals("JUANVI LLAVADOR", menuItem.asText());
		menuItem.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertValue("seller.name", "JUANVI LLAVADOR");
		assertValue("seller.level.id", "A");
		assertDescriptionValue("seller.level.id", "MANAGER");
		assertNoEditable("seller.name");
		assertNoEditable("seller.level");
	}
		
}
