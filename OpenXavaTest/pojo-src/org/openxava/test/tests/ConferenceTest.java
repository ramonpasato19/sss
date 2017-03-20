package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ConferenceTest extends ModuleTestBase {
	
	public ConferenceTest(String testName) {
		super(testName, "Conference");		
	}
	
	public void testEmbeddedCollectionsOfSameType() throws Exception { 
		execute("CRUD.new");
		setValue("name", "THE OPENXAVA CONFERENCE"); 
		
		assertCollectionRowCount("mainTracks", 0);
		execute("Collection.new", "viewObject=xava_view_mainTracks");
		setValue("name", "INTRODUCTION TO OPENXAVA");
		execute("Collection.save");
		assertCollectionRowCount("mainTracks", 1);
		
		assertCollectionRowCount("secondaryTracks", 0);
		execute("Collection.new", "viewObject=xava_view_secondaryTracks");
		setValue("name", "CREATING CUSTOM EDITORS");
		execute("Collection.save");
		assertCollectionRowCount("secondaryTracks", 1);
		assertCollectionRowCount("mainTracks", 1);
		
		execute("Collection.edit", "row=0,viewObject=xava_view_mainTracks");
		assertNoErrors();
		assertValue("name", "INTRODUCTION TO OPENXAVA");
		closeDialog();
		
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
