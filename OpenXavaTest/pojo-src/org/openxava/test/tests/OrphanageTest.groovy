package org.openxava.test.tests;

import java.text.*;

import org.openxava.tests.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

class OrphanageTest extends ModuleTestBase {
	
	OrphanageTest(String testName) {
		super(testName, "Orphanage")		
	}
	
	void testGeneratePDFForACollectionInsideAGroup_dateFormatUsesClientLocale() {
		execute "Mode.detailAndFirst"
		execute "Print.generatePdf", "viewObject=xava_view_orphanage_orphans"; 
		assertNoErrors()
		assertContentTypeForPopup "application/pdf"
		assertPopupPDFLinesCount(6);
		assertPopupPDFLine 1, "Orphans of Orphanage: EL INTERNADO"
		assertPopupPDFLine 2, "Name"
		assertPopupPDFLine 3, "JUAN"
		assertPopupPDFLine 4, "ANTONIO"
		
		// print only the selected
		checkRowCollection "orphans", 1
		execute "Print.generatePdf", "viewObject=xava_view_orphanage_orphans"
		assertNoErrors()
		assertContentTypeForPopup "application/pdf"
		assertPopupPDFLinesCount(5);
		assertPopupPDFLine 1, "Orphans of Orphanage: EL INTERNADO"
		assertPopupPDFLine 2, "Name"
		assertPopupPDFLine 3, "ANTONIO"
		
		// Date formatted in English. It's only tested when the server use a non-English locale
		assertPopupPDFLine 4, "Page 1 of 1${currentDateInEnglish}" 
	}
	
	void testOrphanRemovalAsEmbedded() {
		assertEquals 2, Orphan.count()
		execute "Mode.detailAndFirst"
		assertCollectionRowCount "orphans", 2
		execute "Collection.new", "viewObject=xava_view_orphanage_orphans"
		setValue "name", "PEDRO"
		execute "Collection.save"
		assertEquals 3, Orphan.count()
		assertCollectionRowCount "orphans", 3
		execute "Collection.removeSelected", "row=2,viewObject=xava_view_orphanage_orphans"
		assertCollectionRowCount "orphans", 2 
		assertEquals 2, Orphan.count()
		assertMessage "Orphan deleted from database"
	}
	
	void testXavaEditorInCustomView() {
		execute "Mode.detailAndFirst"
		execute "Orphanage.proposeName"
		assertValue "name", "EL INTERNADO"
		setValue "name", "THE ORPHANAGE VII"
		execute "ProposeName.propose"
		assertMessage "I think that THE ORPHANAGE VII is already a good name"
	}
	
	private String getCurrentDateInEnglish() {
		DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date());
	}
	
}
