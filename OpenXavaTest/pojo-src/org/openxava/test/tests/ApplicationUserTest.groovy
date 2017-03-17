package org.openxava.test.tests

import org.openxava.test.model.*;
import org.openxava.tests.*;

import static org.openxava.jpa.XPersistence.*;

/**
 *  
 * @author Jeromy Altuna
 */
class ApplicationUserTest extends ModuleTestBase {
	
	private ApplicationUser user
	private Nickname nickname
		
	ApplicationUserTest(String testName){
		super(testName, "ApplicationUser")
	}
	
	@Override
	protected void setUp() throws Exception {
		createEntities()
		super.setUp()
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown()
		removeEntities()		
	}
	
	void testUniqueConstraintsMessages(){
		assertListRowCount 1
		execute "CRUD.new"
		setValue "nic", "5634AB78"
		execute "CRUD.save"
		assertError "National identity card is already registered"
		setValue "nic", "6634AB76"
		setValue "name", "TIGRAN PETROSIAN"
		setValue "birthdate", "6/17/1929"
		setValue "sex", "0"
		execute "CRUD.save"
		assertError "Very coincident user data"
		setValue "name", "ANATOLY KARPOV"
		setValue "birthdate", "5/23/1951"
		setValue "sex", "0"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 2
		execute "List.orderBy", "property=name"
		execute "Mode.detailAndFirst"
		assertValue "name", "ANATOLY KARPOV"
		execute "Collection.new", "viewObject=xava_view_nicknames"
		setValue "nickname", "POSITIONALGAMER"
		execute "Collection.saveAndStay"
		assertError "The nickname is already registered"
		setValue "nickname", "POSITIONALGAMERII"
		execute "Collection.save"
		assertNoErrors()
		assertCollectionRowCount "nicknames", 1
		assertValueInCollection("nicknames", 0, "nickname", "POSITIONALGAMERII")
		execute "CRUD.delete"
		assertMessage "Application user deleted successfully"
	}
	
	void testUniqueConstraintsMessagesFromNickname(){
		changeModule "Nickname"
		assertListRowCount 1
		execute "CRUD.new"
		setValue "nickname", "POSITIONALGAMER"
		execute "CRUD.save"
		assertError "The nickname is already registered"
		setValue "nickname", "POSITIONALGAMERII"
		execute "Reference.createNew", "model=ApplicationUser,keyProperty=user.nic"
		setValue "nic", "5634AB78"
		setValue "name", "TIGRAN PETROSIAN"
		setValue "birthdate", "6/17/1929"
		setValue "sex", "0"
		execute "NewCreation.saveNew"
		assertError "National identity card is already registered"
		setValue "nic", "6634AB76"	
		setValue "birthdate", "6/17/1929"
		execute "NewCreation.saveNew"
		assertError "Very coincident user data"
		setValue "name", "ANATOLY KARPOV"
		setValue "birthdate", "5/23/1951"
		setValue "sex", "0"
		execute "NewCreation.saveNew"
		assertNoErrors()
		execute "CRUD.save"
		assertMessage "Nickname created successfully"
		execute "Mode.list"
		assertListRowCount 2
		setConditionValues (["POSITIONALGAMERII"])
		execute "CRUD.deleteRow", "row=0"
		changeModule "ApplicationUser"
		assertListRowCount 2
		execute "List.orderBy", "property=name"
		execute "CRUD.deleteRow", "row=0"
		assertListRowCount 1		
	}
	
	private void createEntities(){				
		user = new ApplicationUser()
		user.nic  = "5634AB78"
		user.name = "TIGRAN PETROSIAN"
		user.birthdate = org.openxava.util.Dates.create(17,06,1929)
		user.sex = ApplicationUser.Sex.MALE
		
		nickname = new Nickname()
		nickname.nickname = "POSITIONALGAMER"
		nickname.user = user
		
		getManager().persist user
		getManager().persist nickname
		commit()
	}
	
	private void removeEntities(){
		getManager().remove(getManager().merge(nickname))
		getManager().remove(getManager().merge(user))
		commit()
	}	
}
