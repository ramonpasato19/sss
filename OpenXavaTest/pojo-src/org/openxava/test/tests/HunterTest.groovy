package org.openxava.test.tests

import static org.openxava.jpa.XPersistence.*

import javax.persistence.*
import org.openxava.test.model.Hound
import org.openxava.tests.ModuleTestBase

/**
 *
 * @author Jeromy Altuna
 */
class HunterTest extends ModuleTestBase {
	
	HunterTest(String testName){
		super(testName, "Hunter")
	}
	
	void testCreateHunterWithAtLeastOneHound(){
		createHounds()
		
		assertListRowCount 0
		execute "CRUD.new"
		setValue "name", "HUNTER 1"
		execute "CRUD.save"
		assertError "It's required at least 1 element in Hounds of Hunter"
		execute "Collection.add", "viewObject=xava_view_hounds"
		execute 'AddToCollection.add', 'row=0'
		assertMessagesCount 2
		assertMessage "Hunter created successfully"
		assertMessage "1 element(s) added to Hounds of Hunter"
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "name", "HUNTER 1"
		assertValueInCollection "hounds", 0, "name", "HOUND 1"
		
		removeHounds()
		execute "CRUD.delete"
		assertNoError "Hunter deleted successfully"
	}
		
	void testAddMaximumFourHounds(){
		createHounds()
		
		execute "CRUD.new"
		setValue "name", "HUNTER 1"
		execute "Collection.add", "viewObject=xava_view_hounds"
		checkAll()
		execute "AddToCollection.add"
		assertError "More than 4 items in Hounds of Hunter are not allowed"
		uncheckRow 1
		uncheckRow 3
		execute "AddToCollection.add"
		assertMessage "Hunter created successfully"
		assertMessage "4 element(s) added to Hounds of Hunter"
		execute "Collection.add", "viewObject=xava_view_hounds"
		execute 'AddToCollection.add', 'row=1'
		assertError "More than 4 items in Hounds of Hunter are not allowed"
		execute "AddToCollection.cancel"
		assertCollectionRowCount "hounds", 4
		
		removeHounds()		
		execute "CRUD.delete"
		assertNoErrors()
	}
	
	private void createHounds(){
		(1..6).each {
			getManager().persist new Hound(name : "HOUND $it")	
		}
		commit()
	}
	
	private void removeHounds(){
		Query query = getManager().createQuery "Delete from Hound"
		query.executeUpdate()		
	}
}
