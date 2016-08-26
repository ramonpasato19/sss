package org.openxava.test.tests;

import javax.persistence.*;
import javax.validation.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

class DatesTest extends TestCase {
	
	private Date date;
		
	DatesTest(String name) {
		super(name);
	}
	
	
	protected void setUp() {
		date = Dates.create 8, 10, 2013
	}
		
	void testCreate() {		
		assertTrue date.toString().startsWith("Tue Oct 08 00:00:00")
		assertTrue date.toString().endsWith("2013")
	}
	
	void testSetGetDay() {
		Dates.setDay date, 10
		assertTrue date.toString().startsWith("Thu Oct 10")
		assertTrue date.toString().endsWith("2013")
		assertEquals 10, Dates.getDay(date)
	}
	
	void testSetGetMonth() {
		Dates.setMonth date, 11
		assertTrue date.toString().startsWith("Fri Nov 08")
		assertTrue date.toString().endsWith("2013")
		assertEquals 11, Dates.getMonth(date)
	}
	
	void testSetGetYear() {
		Dates.setYear date, 2014		
		assertTrue date.toString().startsWith("Wed Oct 08")
		assertTrue date.toString().endsWith("2014")
		assertEquals 2014, Dates.getYear(date)
	}
	
	void testAddDays() {
		Dates.addDays date, 3		
		assertTrue date.toString().startsWith("Fri Oct 11")
		assertTrue date.toString().endsWith("2013")
	}
	
	void testLastDayOfYear() {
		date = Dates.lastOfYear(date);
		assertTrue date.toString().startsWith("Tue Dec 31")
		assertTrue date.toString().endsWith("2013")
	}
	
	void testLastOfMonth() {
		date = Dates.lastOfMonth(date);
		assertTrue date.toString().startsWith("Thu Oct 31")
		assertTrue date.toString().endsWith("2013")
	}

	void testFirstOfMonth() {
		date = Dates.firstOfMonth(date);
		assertTrue date.toString().startsWith("Tue Oct 01")
		assertTrue date.toString().endsWith("2013")
	}

}
