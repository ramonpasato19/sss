package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class Product5Test extends CustomizeListTestBase { 
	
	Product5Test(String testName) {
		super(testName, "Product5")		
	}
	
	void testValidationFromSetterOnCreate() { 
		execute "CRUD.new"
		setValue "number", "666"
		setValue "description", "OPENXAVA"
		setValue "family", "2"
		setValue "subfamily.number", "12"
		setValue "unitPrice", "300"
		execute "CRUD.save"
		assertError "You cannot sell OpenXava"
		assertErrorsCount 1
		setValue "description", "ECLIPSE"
		execute "CRUD.save"
		assertError "You cannot sell Eclipse"
		assertErrorsCount 1
	}

	
	// This case can be only reproduced in custom dialog with Product5 (in other Product it works)
	void testDialogActionsAreNotLost() {  
		execute "ExtendedPrint.myReports" 
		assertValueInCollection "columns", 4, 0, "Unit price"
		assertValueInCollection "columns", 4, 4, "No"
		execute "MyReport.editColumn", "row=4,viewObject=xava_view_columns"
		setValue "sum", "true"
		execute "MyReport.saveColumn"
		assertValueInCollection("columns", 4, 4, "Yes"); 
		assertAction "MyReport.generatePdf"
	}
	
	void testDescriptionsListDependsOnEnum() {
		
		execute("CRUD.new");
	
		// Verifying initial state
		String [][] familyValues = [
			[ "", "" ],
			[ "0", "NONE" ],
			[ "1", "SOFTWARE" ],
			[ "2", "HARDWARE" ],
			[ "3", "SERVICIOS" ]
		]
		
		assertValidValues("family", familyValues);
		setValue("family", "0");
		
		String [][] voidValues = [
			[ "", "" ]
		];
		
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", voidValues);
		
		// Change value
		setValue("family", "2");
		String [][] hardwareValues = [
			[ "", "" ],
			[ "12", "PC" ],
			[ "13", "PERIFERICOS" ],
			[ "11", "SERVIDORES" ]
		];
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", hardwareValues);
		
		// Changing the value again
		setValue("family", "1");
		String [][] softwareValues = [
			[ "", "" ],
			[ "1", "DESARROLLO" ],
			[ "2", "GESTION" ],
			[ "3", "SISTEMA" ]
		];
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", softwareValues);
	}
	
	
	void testCollectionWithLongNameStoresPreferences() { 
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2
		removeColumn "productDetailsSupplierContactDetails", 1  
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 1
		
		resetModule()
		
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 1 
		execute "List.addColumns", "collection=productDetailsSupplierContactDetails"
		execute "AddColumns.restoreDefault"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2 
		
		resetModule()
		
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2
	}

	void testRememberActionsInList() { 
		String[] listActions = 
		[
			'CRUD.new', 'CRUD.deleteRow', 'CRUD.deleteSelected',
			'Print.generatePdf', 'Print.generateExcel',
			'List.filter', 'List.sumColumn', 'List.orderBy', 'List.hideRows', "List.changeConfigurationName",  
			'List.viewDetail', 
			'Mode.detailAndFirst', 'Mode.split',
			'ExtendedPrint.myReports',
			'ListFormat.select',
			'Product5.goB'
		]
		String[] detailActions =
		[
			"Navigation.previous", "Navigation.first", "Navigation.next",
			"CRUD.delete", "CRUD.new", "CRUD.refresh", "CRUD.save", "CRUD.search",
			"Mode.list", "Mode.split",
			"GalleryNoDialog.edit", "List.filter", "Print.generatePdf", 
			"Collection.removeSelected", "List.orderBy", "Collection.new", 
			"Reference.createNew", "Reference.modify", "Print.generateExcel", 
			"Product5.seeInitial"
		]
		String[] galleryActions =
		[
			"Gallery.addImage", "Gallery.close", "Mode.list", "Mode.split" 
		]
		
		// list -> detail -> list
		assertActions(listActions) 
		execute("Product5.goB")
		assertAction("Product5.goA")
		assertNoAction("Product5.goB")
		
		execute("List.viewDetail", "row=0")
		assertActions(detailActions)
		
		execute("Mode.list")
		assertAction("Product5.goA") 
		assertNoAction("Product5.goB")
		assertAction("CRUD.new")
		
		// list -> detail -> gallery editor -> list
		execute("List.viewDetail", "row=0")
		assertNoErrors();
		execute("GalleryNoDialog.edit", "galleryProperty=photos") 
		assertActions(galleryActions)
		
		execute("Mode.list")
		assertAction("Product5.goA")
		assertNoAction("Product5.goB")
		assertAction("CRUD.new")
	}
	
	
	/* it fails after you execute an action with addActions or removeActions */
	void testDialogAfterAddRemoveActions() throws Exception{
		assertAction "Product5.goB"
		execute "List.viewDetail", "row=0"
		assertAction "Navigation.first"
		execute "Product5.seeInitial"
		assertNoErrors()
		assertAction "Dialog.cancel"
		execute "Dialog.cancel"
		assertAction "Navigation.first"
	}
}
