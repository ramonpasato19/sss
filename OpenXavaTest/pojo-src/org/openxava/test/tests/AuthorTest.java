package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

import com.gargoylesoftware.htmlunit.html.*;

/**
 * Create on 09/06/2011 (16:04:34)
 * @author Ana Andres
 */
public class AuthorTest extends CustomizeListTestBase { 
	
	public AuthorTest(String testName) {
		super(testName, "Author");		
	}
	
	public void testRemoveColumnAfterFiltering() throws Exception {
		assertListRowCount(2);
		assertListColumnCount(2);		
		setConditionValues("J");
		execute("List.filter");
		assertListRowCount(1);
		removeColumn(1); 
		assertListRowCount(1);
		assertListColumnCount(1);
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(2);
		assertListRowCount(1); 
	}
	
	public void testAddRemoveActionsForProperty() throws Exception { 
		execute("Mode.detailAndFirst");
		assertNoAction("Author.addSuffix");
		execute("Author.showAddSuffix");
		assertAction("Author.addSuffix");
		assertValue("author", "JAVIER PANIZA");
		execute("Author.addSuffix", "xava.keyProperty=author");
		assertValue("author", "JAVIER PANIZA LUCAS");
		assertAction("Author.addSuffix");
		execute("Author.hideAddSuffix");
		assertNoAction("Author.addSuffix");
	}
	
	public void testOverwritingDefaultSearch() throws Exception {
		execute("Mode.detailAndFirst");
		assertMessage("Showing author JAVIER PANIZA");
		assertValue("author", "JAVIER PANIZA");
		execute("Navigation.next");
		assertMessage("Showing author MIGUEL DE CERVANTES");
		assertValue("author", "MIGUEL DE CERVANTES");
		execute("CRUD.search");
		setValue("author", "JAVIER PANIZA");
		execute("Search.search");
		assertMessage("Showing author JAVIER PANIZA");
		assertValue("author", "JAVIER PANIZA");
		execute("Mode.list");
		execute("List.viewDetail", "row=1");
		assertMessage("Showing author MIGUEL DE CERVANTES");
		assertValue("author", "MIGUEL DE CERVANTES");
	}
	

	public void testCollectionViewWithGroup_getMapValuesFromList() throws Exception {
		assertLabelInList(0, "Author");
		assertValueInList(1, 0, "MIGUEL DE CERVANTES");
		execute("List.viewDetail", "row=1");
		assertCollectionRowCount("humans", 1);

		execute("Author.showAllAuthors", "viewObject=xava_view_humans");
		assertMessage("PEPE, MALE");
		checkRowCollection("humans", 0);
		execute("Author.showSelectedAuthors", "viewObject=xava_view_humans");
		assertMessage("PEPE, MALE");
		
		execute("Collection.view", "row=0,viewObject=xava_view_humans");
		assertNoErrors();
		assertDialog();
	}
 
	
	public void testCustomMessageWithBeanValidationJSR303() throws Exception {
		execute("CRUD.new");
		setValue("author", "PEPE");
		execute("CRUD.save");
		assertError("Sorry, but PEPE is not a good name for an author");  
	}

}
