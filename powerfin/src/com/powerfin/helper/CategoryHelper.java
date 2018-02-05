package com.powerfin.helper;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class CategoryHelper {

	public final static String BALANCE_CATEGORY = "BALANCE";
	public final static String ADVANCE_CATEGORY = "ADVANCE";
	public final static String ADVANCE_SALE_PORTFOLIO_CATEGORY = "ADVSALPORT";
	public final static String CHECK_CATEGORY = "CHECK";
	public final static String INFOREX_CATEGORY = "INFOREX";
	public final static String EXFOREX_CATEGORY = "EXFOREX";
	
	public final static String CAPITAL_CATEGORY = "CAPITAL";
	public final static String SALE_CAPITAL_CATEGORY = "SCAPITAL";
	public final static String CAPDIF_CATEGORY = "CAPDIF";
	public final static String ORDER_CAPITAL_CATEGORY = "OCAPITAL";
	public final static String ORDER_SALE_CAPITAL_CATEGORY = "OSCAPITAL";
	
	public final static String INTEREST_PR_CATEGORY = "INTERESTPR";
	public final static String INTEREST_IN_CATEGORY = "INTERESTIN";
	public final static String INTEREST_EX_CATEGORY = "INTERESTEX";
	public final static String INTDIF_CATEGORY = "INTDIF";
	
	public final static String COLLECTION_FEE_IN_CATEGORY = "COLLEFEEIN";
	public final static String COLLECTION_FEE_RE_CATEGORY = "COLLEFEERE";
	public final static String RECEIVABLE_FEE_IN_CATEGORY = "RECEIFEEIN";
	public final static String RECEIVABLE_FEE_RE_CATEGORY = "RECEIFEERE";
	public final static String LEGAL_FEE_IN_CATEGORY = "LEGALFEEIN";
	public final static String LEGAL_FEE_RE_CATEGORY = "LEGALFEERE";
	
	public final static String DEFAULT_INTEREST_IN_CATEGORY = "DEFINTERIN";
	public final static String DEFAULT_INTEREST_EX_CATEGORY = "DEFINTEREX";
	
	public final static String INSURANCE_RECEIVABLE_CATEGORY = "INSURANRE";
	public final static String INSURANCE_PAYABLE_CATEGORY = "INSURANPA";
	public final static String MORTGAGE_RECEIVABLE_CATEGORY = "MORTGAGERE";
	public final static String MORTGAGE_PAYABLE_CATEGORY = "MORTGAGEPA";
	
	public final static String PURCHASE_SPREAD_PR_CATEGORY = "PURSPRPR";
	public final static String PURCHASE_SPREAD_PR_CATEGORY_LIABILITY = "PURSPRPR2";
	public final static String PURCHASE_SPREAD_EX_CATEGORY = "PURSPREX";
	public final static String PURCHASE_SPREAD_IN_CATEGORY = "PURSPRIN";
	
	public final static String UTILITY_SALE_PORTFOLIO_PR_CATEGORY = "UTLSPRPR";
	public final static String UTILITY_SALE_PORTFOLIO_IN_CATEGORY = "UTLSPRIN";
	
	public final static String COST_CATEGORY = "COST";
	public final static String SALE_COST_CATEGORY = "SCOST";
	public final static String DISCOUNT_CATEGORY = "DISCOUNT";
	
	public static Category getCategoryById(String categoryId)
	{
		return XPersistence.getManager().find(Category.class, categoryId);
	}
	
	public static Category getBalanceCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.BALANCE_CATEGORY);
	}
	
	public static Category getAdvanceCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.ADVANCE_CATEGORY);
	}
	
	public static Category getCheckCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.CHECK_CATEGORY);
	}
	public static Category getInForexCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.INFOREX_CATEGORY);
	}
	public static Category getExForexCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.EXFOREX_CATEGORY);
	}
	public static Category getCostCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.COST_CATEGORY);
	}
	public static Category getSaleCostCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.SALE_COST_CATEGORY);
	}
	public static Category getDiscountCategory()
	{
		return CategoryHelper.getCategoryById(CategoryHelper.DISCOUNT_CATEGORY);
	}
	@SuppressWarnings("rawtypes")
	public static boolean getAllowsNegativeBalance(Account account, Category category) {
		
		List results = XPersistence.getManager().createQuery("select o from CategoryAccount o "
				+ "where o.category = :category "
				+ "and o.account = :account")
				.setParameter("category", category)
				.setParameter("account", account)
				.getResultList();
		
		if(!results.isEmpty())
		{
			if (results.size()>1)
				throw new InternalException("multiple_category_accounts", category.getCategoryId(), account.getAccountId());
			
			CategoryAccount ca = (CategoryAccount)results.get(0); 
			if (ca.getAllowsNegativeBalance().equals(Types.YesNoIntegerType.YES))
				return true;
		}
		
		results = XPersistence.getManager().createQuery("select o from CategoryProduct o "
				+ "where o.category = :category "
				+ "and o.product = :product")
				.setParameter("category", category)
				.setParameter("product", account.getProduct())
				.getResultList();
		
		if(!results.isEmpty())
		{
			if (results.size()>1)
				throw new InternalException("multiple_category_products", category.getCategoryId(), account.getProduct().getProductId()+"-"+account.getProduct().getName());
			
			CategoryProduct cp = (CategoryProduct)results.get(0); 
			if (cp.getAllowsNegativeBalance().equals(Types.YesNoIntegerType.YES))
				return true;
		}

		if (category.equals(Types.YesNoIntegerType.YES))
			return true;
		
		return false;
	}
}
