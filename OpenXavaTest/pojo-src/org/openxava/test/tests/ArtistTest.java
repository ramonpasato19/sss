package org.openxava.test.tests;

import java.util.Locale;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ArtistTest extends ModuleTestBase {
	
	public ArtistTest(String testName) {
		super(testName, "Artist");		
	}
	
	public void testBeanValidationJSR303_focusOnList_dialogFromOnChangeAction() throws Exception {
		// Focus on list
		assertFocusOn("conditionValue___0");
		execute("List.filter"); 
		assertFocusOn("conditionValue___0");
		
		// Bean Validation JSR 303
		execute("Mode.detailAndFirst");
		setValue("age", "99");		
		execute("CRUD.save");		
		assertError("99 is not a valid value for Age of Artist: tiene que ser menor o igual que 90"); 

		assertErrorImage();
		
		// Dialog from OnChange action
		assertExists("age");
		setValue("name", "CHARLOT");				
		assertDialog();
		assertDialogTitle("Are you sure to change the name?");
		assertValue("name", "CHARLOT");
		assertNotExists("age");		
	}
	
	private void assertErrorImage() throws Exception {
		assertTrue("Error image not present", getHtml().contains("<i class=\"ox-error-icon mdi mdi-alert-circle\"")); 
	}
	
}
