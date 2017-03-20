package com.powerfin.helper;

import java.math.BigDecimal;
import org.openxava.jpa.XPersistence;
import com.powerfin.model.Account;
import com.powerfin.model.AccountItem;
import com.powerfin.model.UnitMeasure;
import com.powerfin.model.types.*;

public class AccountItemHelper {



	public static final String ACCOUNT_ITEM_PRODUCT_TYPE = "104";

	public static AccountItem createAccountItem(
			Account account, String accountId, String alternateCode,BigDecimal cost,
			String description, Types.YesNoIntegerType inventoried, BigDecimal maximumQuantity,
			BigDecimal minimalQuantity, String name, byte[] picture, BigDecimal price,
			BigDecimal retailPrice, BigDecimal taxPrice,
			UnitMeasure unitMeasureBean, BigDecimal averageValue
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
		a.setAverageValue(averageValue);
		XPersistence.getManager().persist(a);
		return a;
	}

	public static AccountItem createAccountItem(AccountItem accountItem)  throws Exception
	{
		return createAccountItem(accountItem.getAccount(), accountItem.getAccountId(), null,accountItem.getCost(),
				accountItem.getDescription(),accountItem.getInventoried(), accountItem.getMaximumQuantity(),
				accountItem.getMinimalQuantity(), accountItem.getName(), accountItem.getPicture(), accountItem.getPrice(),
				accountItem.getRetailPrice(), accountItem.getTaxPrice(),
				accountItem.getUnitMeasureBean(), accountItem.getAverageValue());
	}

}
