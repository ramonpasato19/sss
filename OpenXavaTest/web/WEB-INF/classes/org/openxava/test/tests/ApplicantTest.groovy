package org.openxava.test.tests;

import org.apache.commons.lang.*;
import org.openxava.tests.*;
import org.openxava.util.*;
import org.openxava.application.meta.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

class ApplicantTest extends ModuleTestBase {
	
	private boolean modulesLimit = true;
	
	ApplicantTest(String testName) {
		super(testName, "Applicant")		
	}
	
	void testModulesMenu() { 
		modulesLimit = false
		resetModule()
		
		assertModulesCount 15
		assertFirstModuleInMenu "Abstract wall", "Abstract wall" // Not in i18n, to test a case 

		HtmlElement searchBox = htmlPage.getHtmlElementById("search_modules_text")
		searchBox.type "CA"
		assertEquals "CA", searchBox.getAttribute("value")
		webClient.waitForBackgroundJavaScriptStartingBefore 10000
		assertModulesCount 15
		assertFirstModuleInMenu "Academic years", "Academic years management"  
		
		HtmlAnchor loadMoreModules = htmlPage.getHtmlElementById("more_modules").getParentNode()
		loadMoreModules.click();
		webClient.waitForBackgroundJavaScriptStartingBefore 10000
		assertModulesCount 19 // We have to adjust this when we add new modules that content "ca"
		
		searchBox.type " \b"
		assertEquals "", searchBox.getAttribute("value")
		webClient.waitForBackgroundJavaScriptStartingBefore 10000
		assertModulesCount 15
		
		loadMoreModules = htmlPage.getHtmlElementById("more_modules").getParentNode()
		loadMoreModules.click()
		webClient.waitForBackgroundJavaScriptStartingBefore 10000
		assertTrue getModulesCount() > 300 
	}

	private assertFirstModuleInMenu(String expectedName, String expectedDescription) {
		HtmlElement module = htmlPage.getElementById("modules_list_popup").getElementsByAttribute("div", "class", "module-row ").getAt(0)
		HtmlElement moduleName = module.getElementsByAttribute("div", "class", "module-name").getAt(0)
		assertEquals expectedName, moduleName.asText()
		HtmlElement moduleDescription = module.getElementsByAttribute("div", "class", "module-description").getAt(0)
		assertEquals expectedDescription, moduleDescription.asText()
	}

	void testPolymorphicReferenceFromBaseClass() {
		execute "Mode.detailAndFirst"
		assertNoErrors()
		assertValue "name", "JUANILLO"
		assertValue "skill.description", "PROGRAMMING"
		assertValue "skill.language", "JAVA"
		assertValue "platform", "MULTIPLATFORM"		 
	}
	
	void testHtmlHeadNotDuplicated() {
		String html = getHtmlPage().getWebResponse().getContentAsString()
		assertEquals 1, StringUtils.countMatches(html, "<head>")
	}
	
	@Override
	protected String getModuleURL() throws XavaException { 
		return modulesLimit?super.getModuleURL():"http://" + getHost() + ":" + getPort() + "/OpenXavaTest/modules/Applicant";
	}
	
	private void assertModulesCount(int expectedCount) {
		assertEquals expectedCount, getModulesCount()
	}
	
	private int getModulesCount() {
		return htmlPage.getElementById("modules_list_popup").getElementsByAttribute("div", "class", "module-name").size()
	}

			
}
