package com.powerfin.helper;

import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.model.Account;
import com.powerfin.model.Person;

public class PersonHelper {

	public final static String NATURAL_PERSON="NAT";
	public final static String LEGAL_PERSON="LEG";
	
	@SuppressWarnings("unchecked")
	public static Account getDiscountVoucherAccount(Person p) throws Exception
	{
		String discountVoucherProductId = ParameterHelper.getValue("DISCOUNT_VOUCHER_PRODUCT_ID");
		List<Account> accounts = XPersistence.getManager().createQuery("SELECT o FROM Account o "
				+ "WHERE o.person.personId = :personId "
				+ "AND o.product.productId = :productId ")
		.setParameter("personId", p.getPersonId())
		.setParameter("productId", discountVoucherProductId)
		.getResultList();
		
		Account account = accounts.get(0);

		if (account!=null && account.getAccountId()!=null)
		{
			return account;
		}
		else
		{
			throw new OperativeException("person_dont_have_discount_voucher_account");
		}
	}
}
