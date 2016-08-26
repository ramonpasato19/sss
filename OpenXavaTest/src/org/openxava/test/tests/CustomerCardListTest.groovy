package org.openxava.test.tests

import javax.persistence.*

import org.openxava.tests.*
import org.openxava.util.*

import com.gargoylesoftware.htmlunit.html.*

import static org.openxava.jpa.XPersistence.*


/**
 * @author Javier Paniza
 */

class CustomerCardListTest extends ModuleTestBase {
	
	CustomerCardListTest(String testName) {
		super(testName, "CustomerCardList");				
	}

	void testCustomEditorForTab() {		
		assertTrue(html.contains("Gonzalo Gonzalez(43)"))
	}	
								
}
