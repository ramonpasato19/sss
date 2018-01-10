package com.powerfin.helper;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;
import com.powerfin.util.UtilApp;

public class BookAccountHelper {

	@SuppressWarnings("rawtypes")
	public static String getBookAccountParametrized(Account account, Category category) {
		String bookAccount = null;
		
		List results = XPersistence.getManager().createQuery("select o.bookAccount.bookAccountId from Balance o "
				+ "where o.category = :category "
				+ "and o.account = :account "
				+ "and o.toDate = :accountingDate")
				.setParameter("category", category)
				.setParameter("account", account)
				.setParameter("accountingDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.getResultList();
		
		if(!results.isEmpty())
		    bookAccount = (String)results.get(0);
		
		if (bookAccount!=null)
			return bookAccount;
		
		results = XPersistence.getManager().createQuery("select o.bookAccount from CategoryAccount o "
				+ "where o.category = :category "
				+ "and o.account = :account")
				.setParameter("category", category)
				.setParameter("account", account)
				.getResultList();
		
		if(!results.isEmpty())
		    bookAccount = (String)results.get(0);

		if (bookAccount!=null)
			return bookAccount;
		
		results = XPersistence.getManager().createQuery("select o.bookAccount from CategoryProduct o "
				+ "where o.category = :category "
				+ "and o.product = :product")
				.setParameter("category", category)
				.setParameter("product", account.getProduct())
				.getResultList();
		
		if(!results.isEmpty())
		    bookAccount = (String)results.get(0);

		if (bookAccount!=null)
			return bookAccount;
		
		bookAccount = category.getBookAccount();
		
		return bookAccount;
	}
	
	public static BookAccount getBookAccount(String bookAccount)
	{
		return XPersistence.getManager().find(BookAccount.class, bookAccount);
	}

}
