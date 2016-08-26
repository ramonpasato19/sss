package org.openxava.test.calculators;

import java.rmi.*;
import java.util.*;

import org.openxava.calculators.*;
import org.openxava.hibernate.XHibernate;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class FellowCarriersCalculator implements IModelCalculator {

	private ICarrier carrier;

	public Object calculate() throws Exception {
		if (carrier == null || carrier.getWarehouse() == null) return Collections.EMPTY_LIST; 
		int warehouseZoneNumber = carrier.getWarehouse().getZoneNumber();
		int warehouseNumber = carrier.getWarehouse().getNumber();
		// In this case we use an 'if' to illustrate the use of JPA and Hibernate
		// for implementing a calculator in the same code snippet
		// But, generally it's better to have two implementations in two calculators.
		if (XavaPreferences.getInstance().isJPAPersistence()) {
			javax.persistence.EntityManager manager = XPersistence.getManager();		
			javax.persistence.Query query = manager.createQuery("from Carrier as o where " +
					"o.warehouse.zoneNumber = :warehouseZone AND " +
					"o.warehouse.number = :warehouseNumber AND " +
					"NOT (o.number = :number)");
			query.setParameter("warehouseZone", new Integer(warehouseZoneNumber));
			query.setParameter("warehouseNumber", new Integer(warehouseNumber));
			query.setParameter("number", new Integer(carrier.getNumber()));
			return query.getResultList();			
		}
		else {
			org.hibernate.Session session = XHibernate.getSession();		
			org.hibernate.Query query = session.createQuery("from Carrier as o where " +
					"o.warehouse.zoneNumber = :warehouseZone AND " +
					"o.warehouse.number = :warehouseNumber AND " +
					"NOT (o.number = :number)");
			query.setInteger("warehouseZone", warehouseZoneNumber);
			query.setInteger("warehouseNumber", warehouseNumber);
			query.setInteger("number", carrier.getNumber());
			return query.list();
		}
	}

	public void setModel(Object entity) throws RemoteException {
		carrier = (ICarrier) entity;		
	}

}
 