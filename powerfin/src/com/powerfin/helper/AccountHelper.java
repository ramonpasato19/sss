package com.powerfin.helper;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class AccountHelper {

	public final static String AUTO_ACCOUNT_ID = "AUTO";
	public final static String DEFAULT_OPERATING_CONDITION_ID = "001";
	
	public static Account updateAccount(Account account)  throws Exception 
	{
		if (account.getAccountStatus()==null)
			throw new InternalException("account_status_is_required_for_update_account");
		
		if (account.getPerson()==null)
			throw new InternalException("person_is_required_for_update_account");
		
		Account accountToModify = XPersistence.getManager().find(Account.class, account.getAccountId());
		accountToModify.setPerson(account.getPerson());
		if(account.getName()==null){
			accountToModify.setName(account.getPerson().getName());
		}else{
			accountToModify.setName(account.getName());
		}
		accountToModify.setAccountStatus(account.getAccountStatus());
		XPersistence.getManager().merge(accountToModify);
		return accountToModify;
	}
	
	@SuppressWarnings("unchecked")
	public static void validateSingleAccountByPerson(Integer personId, String productId) throws Exception
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
	
	@SuppressWarnings("unchecked")
	public static void validateUniqueAccount(Integer personId, String productId, String code) throws Exception
	{
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT o FROM Account o "
				+ "WHERE o.person.personId=:personId "
				+ "AND o.product.productId=:productId "
				+ "AND o.code=:code")
		.setParameter("personId", personId)
		.setParameter("productId", productId)
		.setParameter("code", code)
		.getResultList();
		
		if (accounts!=null && !accounts.isEmpty())
			throw new OperativeException("account_already_exists",personId, productId, code);
	}

	public static Account createAccount(Account account)  throws Exception 
	{
		return createAccount(account.getAccountId(), account.getProduct(), account.getPerson(), account.getAccountStatus(),
				account.getName(), account.getCode(), account.getAlternateCode(), account.getTransactionalName(), account.getBranch());
	}
	
	public static Account createAccount(String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode)  throws Exception {
		return createAccount(null, productId, personId, accountStatusId, name, code, alternateCode, null, null);
	}
	
	public static Account createAccount(String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode, Integer branchId)  throws Exception {
		return createAccount(null, productId, personId, accountStatusId, name, code, alternateCode, null, branchId);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId)  throws Exception {
		return createAccount(accountId, productId, personId, accountStatusId, null, null, null);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, Integer branchId)  throws Exception {
		return createAccount(accountId, productId, personId, accountStatusId, null, null, null, branchId);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode)  throws Exception {
		return createAccount(accountId, productId, personId, accountStatusId, name, code, alternateCode, null, null);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode, Integer branchId)  throws Exception {
		return createAccount(accountId, productId, personId, accountStatusId, name, code, alternateCode, null, branchId);
	}
	
	public static Account createAccount(String accountId, String productId, Integer personId,
			String accountStatusId, String name, String code, String alternateCode, String transactionalName, Integer branchId)  throws Exception {
		
		AccountStatus accountStatus = null;
		Product product = null;
		Person person = null;
		Branch branch = null;
		
		if (accountStatusId!=null)
			accountStatus = XPersistence.getManager().find(AccountStatus.class, accountStatusId);
		if (productId!=null)
			product = XPersistence.getManager().find(Product.class, productId);
		if (personId!=null)
			person = XPersistence.getManager().find(Person.class, personId);
		if (branchId!=null)
			branch = XPersistence.getManager().find(Branch.class, branchId);
		
		return createAccount(accountId, product, person, accountStatus, name, code, alternateCode, transactionalName, branch);
	}

	public static Account createAccount(String accountId, Product product, Person person, AccountStatus accountStatus,
			String name, String code, String alternateCode, String transactionalName, Branch branch)  throws Exception 
	{
		if (person!=null)
			validateUniqueAccount(person.getPersonId(), product.getProductId(), code);
		else
			validateUniqueAccount(CompanyHelper.getDefaultPerson().getPersonId(), product.getProductId(), code);
		
		Account a = new Account();
		a.setAccountId(accountId);
		a.setProduct(product);
		a.setPerson(person);
		a.setName(name);
		a.setCode(code);
		a.setAlternateCode(alternateCode);
		a.setAccountStatus(accountStatus);	
		a.setTransactionalName(transactionalName);
		a.setBranch(branch==null?UserHelper.getRegisteredBranch():branch);
		XPersistence.getManager().persist(a);
		return a;
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

	public static OperatingCondition getDefaultOperatingCondition() throws Exception
	{
		return XPersistence.getManager().find(OperatingCondition.class, AccountHelper.DEFAULT_OPERATING_CONDITION_ID);
	}
	
	public static BigDecimal getDailyProvision(AccountPaytable quota, Date accountingDate) throws Exception
	{
		BigDecimal finalProvision = BigDecimal.ZERO;
		BigDecimal aux = BigDecimal.ZERO;
		int daysPassed = 0;
		Calendar startDateCalendar = GregorianCalendar.getInstance();
		Calendar accountingDateCalendar = GregorianCalendar.getInstance();
		
		accountingDateCalendar.setTime(accountingDate);
		startDateCalendar.add(Calendar.DAY_OF_MONTH, quota.getProvisionDays()*-1);

		if (quota.getDueDate().before(accountingDate))
			throw new OperativeException("due_date_is_less_than_accounting_date", quota.getAccountId(), quota.getSubaccount(), quota.getDueDate());
		
		if (accountingDateCalendar.before(startDateCalendar))
			throw new OperativeException("accounting_date_is_less_than_or_equal_to_start_date", quota.getAccountId(), quota.getSubaccount(), quota.getDueDate());
		
		if (quota.getSubaccount() == null || quota.getSubaccount() <= 0)
			throw new OperativeException("quota_is_null_or_less_than_zero", quota.getAccountId(), quota.getSubaccount());
		
		if (quota.getInterest() == null || quota.getInterest().compareTo(BigDecimal.ZERO) == 0)
			return finalProvision;
		
		if (quota.getProvisionDays() == null || quota.getProvisionDays() <= 0 )
			throw new OperativeException("provision_days_is_null_or_less_than_zero", quota.getAccountId(), quota.getSubaccount(), quota.getProvisionDays());
		
		long difms=accountingDateCalendar.getTimeInMillis() - startDateCalendar.getTimeInMillis();
		long difd=difms / (1000 * 60 * 60 * 24);
		
		daysPassed = new Long(difd).intValue();
		
		BigDecimal provisionDaily = quota.getInterest().divide(new BigDecimal(quota.getProvisionDays()),6,RoundingMode.HALF_UP);
		finalProvision = provisionDaily.multiply(new BigDecimal(daysPassed));
		/*
		aux2 = provisionDaily.multiply(new BigDecimal(daysPassed-1)).setScale(2, RoundingMode.HALF_UP);
		finalProvision = aux1.subtract(aux2).setScale(2, RoundingMode.HALF_UP);
		*/
		aux = BalanceHelper.getBalance(quota.getAccount().getAccountId(), quota.getSubaccount(), CategoryHelper.INTEREST_PR_CATEGORY);
		if (aux!=null)
			finalProvision = finalProvision.subtract(aux).setScale(2, RoundingMode.HALF_UP);
		
		return finalProvision;
	}
}
