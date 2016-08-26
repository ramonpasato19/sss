package org.openxava.test.calculators;

import java.util.*;

import javax.rmi.*;

import org.openxava.calculators.ICalculator;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

public class ProductsValuesOfSubfamilyCalculator implements ICalculator {

	private int subfamilyNumber;

	public Object calculate() throws Exception {
		// Hibernate and EJB3 JPA are used for illustrative purposed
		// Usually you will use only one of them
		
		if (XavaPreferences.getInstance().isJPAPersistence()) {
			// EJB3 JPA version, returns POJOs.		
			javax.persistence.Query query = XPersistence.getManager().createQuery("from Product2 where subfamily.number = :subfamilyNumber");
			query.setParameter("subfamilyNumber", new Integer(getSubfamilyNumber()));
			return query.getResultList();			
		}
		else {
			// Hiberante version, returns POJOs.		
			org.hibernate.Query query = XHibernate.getSession().createQuery("from Product2 where subfamily.number = :subfamilyNumber");
			query.setInteger("subfamilyNumber", getSubfamilyNumber());
			return query.list();
		}
		
		/* 
		// EJB2 version returns values (not remote objects)
		Collection result = new ArrayList();
		Collection products = Product2Util.getHome().findBySubfamily(getSubfamilyNumber());
		for (Iterator it = products.iterator(); it.hasNext();) {
			Product2Remote product = (Product2Remote) PortableRemoteObject.narrow(it.next(), Product2Remote.class);
			result.add(product.getProduct2Value());
		}
		return result;
		*/
	}

	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

}
