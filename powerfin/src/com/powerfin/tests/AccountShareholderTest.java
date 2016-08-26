package com.powerfin.tests;

import org.openxava.tests.*;

public class AccountShareholderTest extends ModuleTestBase {

	public AccountShareholderTest(String nameTest) {
		super(nameTest, "powerfin", "AccountShareholder");
	}
	
	public void testCreate() throws Exception
	{
		execute("CRUD.new");
		setValue("product.productId","420");
		setValue("person.identification","sssss");
		setValue("percentageParticipation","45");
		execute("CRUD.save"); // Pulsa el botón 'Save'
		assertNoErrors();
	}
}
