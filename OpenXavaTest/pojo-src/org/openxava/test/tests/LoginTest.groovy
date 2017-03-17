package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class LoginTest extends ModuleTestBase {
	
	private static Log log = LogFactory.getLog(LoginTest.class)
	
	LoginTest(String testName) {
		super(testName, "Login")		
	}
	
	void testPasswordStereotype_moduleForTransientClassIsDetailOnlyByDefault() {
		assertFocusOn "user" 
		
		assertNoAction "Mode.list"
		assertNoAction "Mode.detailAndFirst"
		assertNoAction "Mode.split"
	
		setValue "user", "JAVI"
		setValue "password", "x942JlmkK"
		execute "Login.login"
		assertErrorsCount 1 
		
		setValue "user", "JAVI"
		setValue "password", "x8Hjk37mm"
		execute "Login.login"
		assertNoErrors()
		assertMessage "OK"
	}
	
}
