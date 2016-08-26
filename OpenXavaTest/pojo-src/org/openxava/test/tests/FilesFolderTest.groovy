package org.openxava.test.tests

import org.openxava.tests.ModuleTestBase 


/**
 * 
 * @author Javier Paniza
 */
class FilesFolderTest extends ModuleTestBase {
	
	FilesFolderTest(String testName) {
		super(testName, "FilesFolder")		
	}
	
	void testAddingChildrenWithCollectionElementsInACompositeWhoseParentReferenceNotNamedAsEntity() {
		execute "CRUD.new"
		setValue "name", "JUNIT FOLDER"
		execute "Collection.new", "viewObject=xava_view_subfolders"
		setValue "name", "JUNIT SUBFOLDER"
		execute "Collection.add", "viewObject=xava_view_files"
		checkAll()
		execute "AddToCollection.add"
		assertNoErrors()
		assertCollectionRowCount "files", 2
		execute "Collection.save"
		assertNoErrors()
		assertCollectionRowCount "subfolders", 1
		
		execute "CRUD.delete"
		assertNoErrors()
	}
	
}
