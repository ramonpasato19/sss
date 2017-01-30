package com.powerfin.helper;

import java.math.BigDecimal;
import org.openxava.jpa.XPersistence;
import com.powerfin.model.Account;
import com.powerfin.model.AccountItem;
import com.powerfin.model.UnitMeasure;

public class AccountItemHelper {



	public static AccountItem createAccountItem(
			Account account, String accountId, String alternateCode,BigDecimal cost,
			String description,Integer inventoried, BigDecimal maximumQuantity,
			BigDecimal minimalQuantity, String name, byte[] picture, BigDecimal price,
			BigDecimal retailPrice, BigDecimal taxPrice,
			UnitMeasure unitMeasureBean
			)  throws Exception
	{
		AccountItem a = new AccountItem();
		a.setAccount(account);
		a.setAccountId(account.getAccountId());
		a.setCode(alternateCode);
		a.setCost(cost);
		a.setDescription(description);
		a.setInventoried(inventoried);
		a.setMaximumQuantity(maximumQuantity);
		a.setMinimalQuantity(minimalQuantity);
		a.setName(name);
		a.setPicture(picture);
		a.setPrice(price);
		a.setRetailPrice(retailPrice);
		a.setTaxPrice(taxPrice);
		a.setUnitMeasureBean(unitMeasureBean);

		XPersistence.getManager().persist(a);
		return a;
	}

	public static AccountItem createAccountItem(AccountItem accountItem)  throws Exception
	{
		return createAccountItem(accountItem.getAccount(), accountItem.getAccountId(), null,accountItem.getCost(),
				accountItem.getDescription(),accountItem.getInventoried(), accountItem.getMaximumQuantity(),
				accountItem.getMinimalQuantity(), accountItem.getName(), accountItem.getPicture(), accountItem.getPrice(),
				accountItem.getRetailPrice(), accountItem.getTaxPrice(),
				accountItem.getUnitMeasureBean());
	}

}
