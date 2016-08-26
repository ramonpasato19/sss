package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @since 5.2
 * @author Javier Paniza
 */
abstract public class CustomizeListTestBase extends ModuleTestBase {
	
	private String module;
	
	public CustomizeListTestBase(String testName, String module) {
		super(testName, module);
		this.module = module;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void removeColumn(int index) throws Exception {
		removeColumn("list", "xava_tab", index);
	}
	
	protected void removeColumn(String collection, int index) throws Exception {
		removeColumn(collection, "xava_collectionTab_" + collection, index);
	}
	
	protected void moveColumn(int from, int to) throws Exception { 
		moveColumn("list", from, to); 
	}
	
	protected void moveColumn(String collection, int from, int to) throws Exception { 
		// This method does not work for all "from, to" combinations, at least with HtmlUnit 2.15
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement fromCol = table.getRow(0).getCell(from + 2);
		HtmlElement handle = fromCol.getElementsByAttribute("img", "src", "/OpenXavaTest/xava/images/move.png").get(0);
		handle.mouseDown();
		HtmlElement toCol = table.getRow(0).getCell(to + 2);
		HtmlElement elementTo = toCol.getElementsByAttribute("img", "src", "/OpenXavaTest/xava/images/remove.gif").get(0);
		elementTo.mouseMove();
		elementTo.mouseUp();
		Thread.sleep(500);		
	}

	
	private void removeColumn(String collection, String tabObject, int index) throws Exception {
		getWebClient().getOptions().setCssEnabled(true); 
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement header = table.getRow(0).getCell(index + 2);
		HtmlElement image = header.getElementsByAttribute("img", "src", "/OpenXavaTest/xava/images/remove.gif").get(0);
		HtmlElement removeLink = image.getEnclosingElement("a");		
		removeLink.click();
		Thread.sleep(500);		
	}

}
