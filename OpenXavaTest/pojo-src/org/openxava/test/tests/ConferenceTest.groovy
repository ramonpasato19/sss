package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class ConferenceTest extends ModuleTestBase {
	
	ConferenceTest(String testName) {
		super(testName, "Conference")		
	}
	
	void testEmbeddedCollectionsOfSameType() {
		execute "CRUD.new"
		setValue "name", "THE OPENXAVA CONFERENCE" 
		
		assertCollectionRowCount "mainTracks", 0
		execute "Collection.new", "viewObject=xava_view_mainTracks"
		setValue "name", "INTRODUCTION TO OPENXAVA"
		execute "Collection.save"
		assertCollectionRowCount "mainTracks", 1
		
		assertCollectionRowCount "secondaryTracks", 0
		execute "Collection.new", "viewObject=xava_view_secondaryTracks"
		setValue "name", "CREATING CUSTOM EDITORS"
		execute "Collection.save"
		assertCollectionRowCount "secondaryTracks", 1
		assertCollectionRowCount "mainTracks", 1
		
		execute "CRUD.delete"
		assertNoErrors() 
		
	}
			
}
