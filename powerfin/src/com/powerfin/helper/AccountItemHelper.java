package com.powerfin.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.InternalException;
import com.powerfin.model.Account;
import com.powerfin.model.AccountItem;
import com.powerfin.model.AccountItemBranch;
import com.powerfin.model.Branch;
import com.powerfin.model.UnitMeasure;
import com.powerfin.model.types.Types;

public class AccountItemHelper {

	public static final String ACCOUNT_ITEM_PRODUCT_TYPE = "104";

	public static void updateAverageCost(AccountItem accountItem, Branch branch) throws Exception
	{
		BigDecimal balance = BalanceHelper.getBalance(accountItem.getAccount().getAccountId(), 0, CategoryHelper.COST_CATEGORY, branch.getBranchId(), null);
		BigDecimal quantity = BalanceHelper.getStock(accountItem.getAccount().getAccountId(), 0, CategoryHelper.COST_CATEGORY, branch.getBranchId(), null);
		
		AccountItemBranch accountItemBranch = AccountItemHelper.findOrCreateAccountItemBranch(accountItem, branch);
		
		if (quantity == null || quantity.compareTo(BigDecimal.ZERO)<0)
			throw new InternalException("item_quantity_is_negative_or_null", accountItem.getCode(), branch.getBranchId());
		
		if (balance == null || balance.compareTo(BigDecimal.ZERO)<0)
			throw new InternalException("item_balance_is_negative_or_null", accountItem.getCode(), branch.getBranchId());
		
		if (quantity.compareTo(BigDecimal.ZERO)>0 && balance.compareTo(BigDecimal.ZERO)>0)
		{
			accountItemBranch.setAverageCost(balance.divide(quantity, 6, RoundingMode.HALF_UP));
			XPersistence.getManager().persist(accountItemBranch);
		}
	}
	
	public static AccountItemBranch findOrCreateAccountItemBranch(AccountItem accountItem, Branch branch)
	{
		for (AccountItemBranch aib : accountItem.getAccountItemBranch())
		{
			if (aib.getBranch().getBranchId() == branch.getBranchId())
				return aib;
		}
		
		AccountItemBranch accountItemBranch = new AccountItemBranch();
		accountItemBranch.setAccountItem(accountItem);
		accountItemBranch.setBranch(branch);
		accountItemBranch.setMinimumStock(BigDecimal.ONE);
		accountItemBranch.setMaximumStock(BigDecimal.TEN);
		accountItemBranch.setAverageCost(BigDecimal.ZERO);
		XPersistence.getManager().persist(accountItemBranch);
		
		return accountItemBranch;
	}
	
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
