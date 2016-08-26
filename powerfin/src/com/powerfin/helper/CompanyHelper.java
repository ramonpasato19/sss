package com.powerfin.helper;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;
import com.powerfin.model.Currency;

public class CompanyHelper {

	public final static Integer COMPANY_ID = 1;
	public static Currency getDefaultCurrency()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return company.getOfficialCurrency();
	}
	
	public static Person getDefaultPerson()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return company.getPerson();
	}
	
	public static Date getCurrentAccountingDate()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return company.getAccountingDate();
	}
	
	public static String getSchema()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return company.getOxorganizationId();
	}
	
	public static String getLogoName()
	{
		return "logo-efc.png";
	}
}
