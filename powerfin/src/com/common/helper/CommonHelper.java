package com.common.helper;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class CommonHelper {

	public final static Integer COMPANY_ID = 1;
	public static Person getDefaultPerson()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CommonHelper.COMPANY_ID); 
		return company.getPerson();
	}
	
	public static Date getCurrentAccountingDate()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CommonHelper.COMPANY_ID); 
		return company.getAccountingDate();
	}
	
	public static String getLogoName()
	{
		return "logo.png";
	}
	
	public static int getOrganizationId()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CommonHelper.COMPANY_ID); 
		return company.getPerson().getPersonId();
	}
}
