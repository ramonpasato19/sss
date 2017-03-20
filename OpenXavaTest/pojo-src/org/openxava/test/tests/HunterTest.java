package org.openxava.test.tests;

import static org.openxava.jpa.XPersistence.*;

import java.util.*;

import javax.persistence.*;

import org.openxava.test.model.*;
import org.openxava.tests.ModuleTestBase;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *
 * @author Jeromy Altuna
 */
public class HunterTest extends ModuleTestBase {
	
	public HunterTest(String testName){
		super(testName, "Hunter");
	}
	
	public void testCreateHunterWithAtLeastOneHound() throws Exception {
		createHounds();
		
		assertListRowCount(0);
		execute("CRUD.new");
		setValue("name", "HUNTER 1");
		execute("CRUD.save");
		assertError("It's required at least 1 element in Hounds of Hunter");
		execute("Collection.add", "viewObject=xava_view_hounds");
		execute("AddToCollection.add", "row=0");
		assertMessagesCount(2);
		assertMessage("Hunter created successfully");
		assertMessage("1 element(s) added to Hounds of Hunter");
		execute("Mode.list");
		assertListRowCount(1);
		execute("Mode.detailAndFirst");
		assertValue("name", "HUNTER 1");
		assertValueInCollection("hounds", 0, "name", "HOUND 1");
		
		removeHounds();
		execute("CRUD.delete");
		assertNoError("Hunter deleted successfully");
	}
		
	public void testAddMaximumFourHounds() throws Exception {
		createHounds();
		
		execute("CRUD.new");
		setValue("name", "HUNTER 1");
		execute("Collection.add", "viewObject=xava_view_hounds");
		checkAll();
		execute("AddToCollection.add");
		assertError("More than 4 items in Hounds of Hunter are not allowed");
		uncheckRow(1);
		uncheckRow(3);
		execute("AddToCollection.add");
		assertMessage("Hunter created successfully");
		assertMessage("4 element(s) added to Hounds of Hunter");
		execute("Collection.add", "viewObject=xava_view_hounds");
		execute("AddToCollection.add", "row=1");
		assertError("More than 4 items in Hounds of Hunter are not allowed");
		execute("AddToCollection.cancel");
		assertCollectionRowCount("hounds", 4);
		
		removeHounds();		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testFilterEmptyValuesInCollection() throws Exception {
		createHunter();
		
		reload();
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertFalse(isNotVisibleConditionValue(0));
		assertFalse(isNotVisibleConditionValue(1));
		
		// Filter String
		setConditionComparators("hounds", new String [] {"empty_comparator"});		
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 0);
		assertTrue(isNotVisibleConditionValue(0));
		
		// Filter Date
		setConditionComparators("hounds", new String [] {"=", "empty_comparator"});
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 1);
		assertTrue(isNotVisibleConditionValue(1));
		assertFalse(isNotVisibleConditionValue(0));
		
		removeHounds();
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testFilterNotEmptyValuesInCollection() throws Exception {
		createHunter();
		
		reload();
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertFalse(isNotVisibleConditionValue(0));
		assertFalse(isNotVisibleConditionValue(1));
		
		// Filter String
		setConditionComparators("hounds", new String [] {"not_empty_comparator"});		
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 2);
		assertTrue(isNotVisibleConditionValue(0));
		
		// Filter Date
		setConditionComparators("hounds", new String [] {"=", "not_empty_comparator"});
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 1);
		assertTrue(isNotVisibleConditionValue(1));
		assertFalse(isNotVisibleConditionValue(0));
		
		removeHounds();
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private void createHounds() {
		for (int i = 1; i < 7; i++) {
			getManager().persist(new Hound("HOUND " + i));
		}
		commit();
	}
	
	private void createHunter() {
		Hound hound1 = new Hound("HOUND 1", new Date());
		Hound hound2 = new Hound("HOUND 2");
		getManager().persist(hound1);
		getManager().persist(hound2);
		
		Hunter hunter = new Hunter("HUNTER");
		hunter.addHound(hound1);
		hunter.addHound(hound2);
		getManager().persist(hunter);
		
		commit();
	}
	
	private void removeHounds(){
		Query query = getManager().createQuery("Delete from Hound");
		query.executeUpdate();		
	}
	
	private boolean isNotVisibleConditionValue(int index) {
		String idConditionValue = 
				"ox_" + getXavaJUnitProperty("application") + 
				"_Hunter__xava_collectionTab_hounds_conditionValue___" + index;
		HtmlElement input = getHtmlPage().getHtmlElementById(idConditionValue); 
		return input.getAttribute("style").contains("display: none");			
	}
}
