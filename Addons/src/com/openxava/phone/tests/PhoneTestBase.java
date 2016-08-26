package com.openxava.phone.tests;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class PhoneTestBase extends ModuleTestBase {
	
	private String module;
	
	public PhoneTestBase(String testName, String moduleName) {
		super(testName, moduleName);
		module = moduleName;
	}
	
	protected boolean _collectionHasFilterHeader(String collection) throws Exception { 
		return true;
	}
	
	protected void assertValueInList(int row, String value) throws Exception {
		super.assertValueInList(row, 1, value);
	}
	
	protected String getValueInList(int row) throws Exception {
		return getValueInList(row, 1);
	}
	
	/**
	 * @since 5.3
	 */	
	protected void assertValueInCollection(String collection, int row, String value) throws Exception { 
		assertValueInCollection(collection, row - 1, 0, value);
	}	

	/**
	 * @since 5.3
	 */		
	protected String getValueInCollection(String collection, int row) throws Exception { 
		return getValueInCollection(collection, row - 1, 0);
	}	
	
	protected String getValueInList(int row, int column) throws Exception {
		return super.getValueInList(row-2, column-1);
	}
	
	protected int getListRowCount() throws Exception {
		return super.getListRowCount() + 2;
	}
	
	protected String getModuleURL() throws XavaException {
		return "http://" + getHost() + ":" + getPort() + "/XavaPro/p/" + module;
	}
	
}
