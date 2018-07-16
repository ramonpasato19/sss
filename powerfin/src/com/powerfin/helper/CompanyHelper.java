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
	
	public static String getName()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return company.getName();
	}
	
	public static File getElectronicSignature()
	{
		Company company = (Company)XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID); 
		return XPersistence.getManager().find(File.class, company.getElectronicSignatureFile());
	}
	
	public static Unity getDefaultUnity() throws Exception
	{
		return XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
	}
	
	public static Branch getDefaultBranch() throws Exception
	{
		return XPersistence.getManager().find(Branch.class, Integer.parseInt(ParameterHelper.getValue("MAIN_BRANCH_ID")));
	}
	
	public static String getLogoName()
	{
		return "logo.png";
	}
}
