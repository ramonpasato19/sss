package org.openxava.test.tests

import org.openxava.tests.*

/**
 *
 * @author Jeromy Altuna
 */
class ExamTest extends ModuleTestBase {
	
	
	ExamTest(String testName){
		super(testName, "Exam")
	}
	
	void testCreateExamWithAtLeastOneQuestion(){
		assertListRowCount 0
		execute "CRUD.new"
		setValue "name", "ADMISSION"
		execute "CRUD.save"
		assertError "It's required at least 1 element in Questioning of Exam"
		execute "Collection.new", "viewObject=xava_view_questioning"
		setValue "name", "QUESTION 1"
		execute "Collection.save"
		assertMessagesCount 2
		assertMessage "Exam created successfully"
		assertMessage "Question created successfully"
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "name", "ADMISSION"
		assertValueInCollection "questioning", 0, "name", "QUESTION 1"
		execute "CRUD.delete"
		assertMessage "Exam deleted successfully"
	}
	
	void testRemoveElementsFromQuestioningAndLeaveAtLeastOneQuestion(){
		assertListRowCount 0
		execute "CRUD.new"
		setValue "name", "ADMISSION"
		execute "Collection.new", "viewObject=xava_view_questioning"
		(1..3).each {
			setValue "name", "$it"
			execute "Collection.saveAndStay"
			assertMessage "Question created successfully" 
		}
		execute "Collection.hideDetail"
		assertValue "name", "ADMISSION"
		assertCollectionRowCount "questioning", 3
		
		checkAllCollection "questioning"
		execute "Collection.removeSelected", "viewObject=xava_view_questioning"
		assertError "It's required at least 1 element in Questioning of Exam" 
		uncheckRowCollection "questioning", 0
		uncheckRowCollection "questioning", 1
		execute "Collection.removeSelected", "viewObject=xava_view_questioning"
		assertMessage "Question deleted from database"
		assertCollectionRowCount "questioning", 2
		execute "Collection.edit", "row=1,viewObject=xava_view_questioning"
		execute "Collection.remove"
		assertMessage "Question deleted from database"
		assertCollectionRowCount "questioning", 1
		execute "Collection.removeSelected", "row=0,viewObject=xava_view_questioning"
		assertError "It's required at least 1 element in Questioning of Exam"
		execute "Collection.edit", "row=0,viewObject=xava_view_questioning"
		execute "Collection.remove"
		assertError "It's required at least 1 element in Questioning of Exam"
		execute "Collection.hideDetail"
		assertCollectionRowCount "questioning", 1
		
		execute "CRUD.delete"
		assertNoErrors()
	}
	
}
