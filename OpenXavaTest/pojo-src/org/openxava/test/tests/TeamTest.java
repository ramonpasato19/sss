package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TeamTest extends ModuleTestBase {
	
	public TeamTest(String testName) {
		super(testName, "Team");		
	}
	
	public void testSearchingWithInheritanceFromAEntityCollectionElement() throws Exception {  		
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_members");
		execute("Team.searchPerson", "keyProperty=person.name");
		assertListRowCount(3);
		execute("ReferenceSearch.choose", "row=0"); 
		assertValue("person.name", "JAVI"); // Must be JAVI because he's a JavaProgrammer, we need that to reproduce a bug
		execute("Team.searchPerson", "keyProperty=person.name");
		assertListRowCount(3);
	}
		
}
