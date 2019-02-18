package com.openxava.phone.tests;

import java.util.*;

import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class PhoneTestBase extends ModuleTestBase {
	
	private String module;
	
	public PhoneTestBase(String testName, String moduleName) {
		super(testName, moduleName);
		module = moduleName;
	}
	
	protected boolean _collectionHasFilterHeader(String collection) throws Exception {
		boolean isCalculated = getHtmlPage()
				               .getByXPath("//input[starts-with(@value, '" + collection + ".__SELECTED__:')]")
				               .size() > 0; 
		return !isCalculated;
	}
	
	protected void assertValueInList(int row, String value) throws Exception {
		assertValueInCollection("list", row, value);  
		
	}
	
	protected String getValueInList(int row) throws Exception {
		return getValueInCollection("list", row); 
	}
	
	/**
	 * @since 5.3
	 */	
	protected void assertValueInCollection(String collection, int row, String value) throws Exception { 
		assertEquals(XavaResources.getString("unexpected_value_in_collection", "", new Integer(row), collection), value, getValueInCollection(collection, row));
	}	

	/**
	 * @since 5.3
	 */		
	protected String getValueInCollection(String collection, int row) throws Exception { 
		HtmlElement el = getCollectionElements(collection).get(row);
		List<HtmlElement> anchors = el.getElementsByTagName("a");
		if (!anchors.isEmpty()) el = anchors.get(0);
		return el.asText();
	}	
	
	protected String getValueInList(int row, int column) throws Exception {
		return super.getValueInList(row-2, column-1);
	}
	
	protected int getListRowCount() throws Exception {
		return getCollectionRowCount("list");
	}
	
	protected int getCollectionRowCount(String collection) throws Exception {		
		int rc = getCollectionElements(collection).size();
		if (rc == 1) {
			HtmlElement el = getCollectionElements(collection).get(0);
			List<HtmlElement> anchors = el.getElementsByTagName("a");
			if (anchors.isEmpty()) rc = 0;
		}
		return rc;
	}
		
	private List<HtmlElement> getCollectionElements(String collection) {
		HtmlElement collectionDiv = getHtmlPage().getHtmlElementById(decorateId(collection));
		return collectionDiv.getElementsByAttribute("div", "class", "phone-list-element");		
	}
	
	protected String getModuleURL() throws XavaException {
		return "http://" + getHost() + ":" + getPort() + "/XavaPro/p/" + module;
	}
		
}
