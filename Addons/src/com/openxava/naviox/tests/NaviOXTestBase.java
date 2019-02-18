package com.openxava.naviox.tests;

import static org.openxava.tests.HtmlUnitUtils.assertElementExists;
import static org.openxava.tests.HtmlUnitUtils.assertElementNotExist;

import java.util.*;

import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *
 * @since 5.7
 * @author Javier Paniza
 */

abstract public class NaviOXTestBase extends ModuleTestBase {
	
	private String module;
	private String moduleURL; 
	
	public NaviOXTestBase(String nameTest, String module) {
		super(nameTest, module);
		this.module = module;
	}
		
	protected void goFolder(int number) throws Exception {
		HtmlElement moduleLink = getModulesListElements("folder").get(number).getEnclosingElement("a");
		moduleLink.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}
	
	
	protected void goFolder(String folderId) {
		getHtmlPage().executeJavaScript("naviox.goFolder('" + folderId + "')");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}
	
	protected void goBack() {
		getHtmlPage().executeJavaScript("naviox.goBack()");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}	
	
	protected void assertModulesCount(int expectedCount) {
		assertEquals(expectedCount, getModulesCount()); 
	}
	
	protected int getModulesCount() { 
		return getModulesListElements("module").size();
	}
	
	protected void assertNoModulesMenu() { 
		assertElementNotExist(getHtmlPage(), "modules_list"); 
	}
	
	protected void assertModulesMenu() { 
		assertElementExists(getHtmlPage(), "modules_list");  
	}

	protected void assertFoldersCount(int expectedCount) {
		int count = getModulesListElements("folder").size();
		assertEquals(expectedCount, count);
	}

	protected void assertFolderIcon(int number, String icon) {
		HtmlAnchor folderLink = (HtmlAnchor) getModulesListElements("folder").get(number).getEnclosingElement("a");
		assertTrue(folderLink.asXml().contains("<i class=\"mdi mdi-" + icon + "\""));
	}
	
	protected void assertModule(int number, String name) {
		HtmlAnchor moduleLink = (HtmlAnchor) getModulesListElements("module").get(number).getEnclosingElement("a");
		String link = moduleLink.getHrefAttribute(); 
		assertTrue(link.endsWith("/" + name + "?init=true")); 
	}
	
	protected void assertFolderLabel(int number, String label) { 
		HtmlElement moduleLink = getModulesListElements("folder").get(number).getEnclosingElement("a");
		assertEquals(label, moduleLink.asText().trim());
	}
	
	private List<HtmlElement> getModulesListElements(String type) {
		return getHtmlPage().getHtmlElementById("modules_list").getElementsByAttribute("div", "class", type + "-name"); 
	}
	
	protected String getModuleURL() throws XavaException { 
		if (moduleURL == null) {
			// Really not needed because /modules/ and /m/ are synonymous, but in this way we test both cases
			return Strings.noLastTokenWithoutLastDelim(super.getModuleURL(), "?").replace("/modules/", "/m/");
		}
		return "http://" + getHost() + ":" + getPort() + "/XavaPro/" + moduleURL; 
	}
	
	protected void setModuleURL(String moduleURL) { 
		this.moduleURL = moduleURL;
	}
		
	protected void loginFailing(String user, String password) throws Exception {
		selectModuleInPage("SignIn");
		setValue("user", user);
		setValue("password", password);
		execute("SignIn.signIn");
		assertError("Unauthorized user");
		assertTrue(getHtml().contains("Sign in"));
		selectModuleInPage(module);
	}
	
}