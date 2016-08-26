package com.powerfin.helper;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class AccountHelper {

	public final static String AUTO_ACCOUNT_ID = "AUTO";
	
	public static Account updateAccount(Account account)  throws Exception 
	{
		if (account.getAccountStatus()==null)
			throw new InternalException("account_status_is_required_for_update_account");
		
		if (account.getPerson()==null)
			throw new InternalException("person_is_required_for_update_account");
		
		Account accountToModify = XPersistence.getManager().find(Account.class, account.getAccountId());
		accountToModify.setPerson(account.getPerson());
		accountToModify.setName(account.getPerson().getName());
		accountToModify.setAccountStatus(account.getAccountStatus());
		XPersistence.getManager().merge(accountToModify);
		return accountToModify;
	}
	
	@SuppressWarnings("unchecked")
	public static void validateUniqueAccount(Integer personId, String productId) throws Exception
	{
		Product product = XPersistence.getManager().find(Product.class, productId);

		if (product.getSingleAccount().equals(Types.YesNoIntegerType.YES))
		{
			List<Account> accounts = XPersistence.getManager().createQuery("SELECT o FROM Account o "
					+ "WHERE o.person.personId=:personId "
					+ "AND o.product.productId=:productId")
			.setParameter("personId", personId)
			.setParameter("productId", productId)
			.getResultList();
			
			if (accounts!=null && !accounts.isEmpty())
				throw new OperativeException("the_person_already_has_an_account",product.getName());
		}
	}

	public static Account createAccount(String accountId, Product product, Person person, AccountStatus accountStatus,
			String name, String code, String alternateCode, String transactionalName)  throws Exception 
	{
		Account a = new Account();
		a.setAccountId(accountId);
		a.setProduct(product);
		a.setPerson(person);
		a.setName(name);
		a.setCode(code);
		a.setAlternateCode(alternateCode);
		a.setAccountStatus(accountStatus);	
		a.setTransactionalName(transactionalName);
		
		XPersistence.getManager().persist(a);
		return a;
	}
	
	public static Account createAccount(Account account)  throws Exception 
	{
		return createAccount(account.getAccountId(), account.getProduct(), account.getPerson(), account.getAccountStatus(),
				account.getName(), account.getCode(), account.getAlternateCode(), account.getTransactionalName());
	}
	
	public static Account createAccount(String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode)  throws Exception {
		
		AccountStatus accountStatus = null;
		Product product = null;
		Person person = null;
		
		if (accountStatusId!=null)
			accountStatus = XPersistence.getManager().find(AccountStatus.class, accountStatusId);
		if (productId!=null)
			product = XPersistence.getManager().find(Product.class, productId);
		if (personId!=null)
			person = XPersistence.getManager().find(Person.class, personId);
		return createAccount(null, product, person, accountStatus, name, code, alternateCode, null);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode, String transactionalName)  throws Exception {
		
		AccountStatus accountStatus = null;
		Product product = null;
		Person person = null;
		
		if (accountStatusId!=null)
			accountStatus = XPersistence.getManager().find(AccountStatus.class, accountStatusId);
		if (productId!=null)
			product = XPersistence.getManager().find(Product.class, productId);
		if (personId!=null)
			person = XPersistence.getManager().find(Person.class, personId);
		return createAccount(accountId, product, person, accountStatus, name, code, alternateCode, transactionalName);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode)  throws Exception {
		
		AccountStatus accountStatus = null;
		Product product = null;
		Person person = null;
		
		if (accountStatusId!=null)
			accountStatus = XPersistence.getManager().find(AccountStatus.class, accountStatusId);
		if (productId!=null)
			product = XPersistence.getManager().find(Product.class, productId);
		if (personId!=null)
			person = XPersistence.getManager().find(Person.class, personId);
		return createAccount(accountId, product, person, accountStatus, name, code, alternateCode, null);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId)  throws Exception {
		return createAccount(accountId, productId, personId, accountStatusId, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public static AccountStatus getDefaultAccountStatusByProduct(Product product)  throws Exception 
	{
		AccountStatus accountStatus = null;
		if (product!=null)
		{
			Query query = XPersistence.getManager().createQuery("from ProductStatus o "
					+ "where o.product = :product "
					+ "and o.byDefault = :byDefault")
					.setParameter("product", product)
					.setParameter("byDefault", Types.YesNoIntegerType.YES);
			List<ProductStatus> productStatus = query.getResultList();
			if (productStatus!=null && !productStatus.isEmpty())
				accountStatus = ((ProductStatus)productStatus.get(0)).getAccountStatus();
		}
		return accountStatus;
	}

}
