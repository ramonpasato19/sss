

// File generated by OpenXava: Thu Jan 26 12:42:00 CET 2017
// Archivo generado por OpenXava: Thu Jan 26 12:42:00 CET 2017

// WARNING: NO EDIT
// OJO: NO EDITAR
// Component: ServiceInvoice		Java interface for entity/Interfaz java para Entidad

package org.openxava.test.model;

import java.math.*;
import java.rmi.RemoteException;


public interface IServiceInvoice  extends org.openxava.model.IModel {	

	// Properties/Propiedades 	
	public static final String PROPERTY_amount = "amount"; 
	java.math.BigDecimal getAmount() throws RemoteException;
	void setAmount(java.math.BigDecimal amount) throws RemoteException; 	
	public static final String PROPERTY_description = "description"; 
	java.lang.String getDescription() throws RemoteException;
	void setDescription(java.lang.String description) throws RemoteException; 	
	public static final String PROPERTY_oid = "oid"; 	
	String getOid() throws RemoteException; 	
	public static final String PROPERTY_number = "number"; 
	int getNumber() throws RemoteException;
	void setNumber(int number) throws RemoteException; 	
	public static final String PROPERTY_year = "year"; 
	int getYear() throws RemoteException;
	void setYear(int year) throws RemoteException;		

	// References/Referencias

	// Methods 


}