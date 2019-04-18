package com.powerfin.actions.accountInvoice;

import java.math.BigDecimal;
import java.util.Map;

import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.model.AccountInvoiceDetail;
import com.powerfin.model.AccountItem;

public class OnModifyItemPurchaseOrder extends OnChangePropertyBaseAction {
	
	@Override
	public void execute() throws Exception {
		
		AccountInvoiceDetail aid = new AccountInvoiceDetail();
		AccountItem ai = new AccountItem();
		getView().setValue("unitMeasure", " ");
		getView().setValue("lastUnitPrice", new BigDecimal("0.00"));	
		
		if (getNewValue() == null)
			return;
		Map<String, String> mapItem = (Map<String, String>) getView().getValue("accountDetail");
		System.out.println("AccountID"+ mapItem.get("accountId").toString());
		try {
	    ai = XPersistence.getManager().find(AccountItem.class, mapItem.get("accountId").toString());
		getView().setValue("unitMeasure", ai.getUnitMeasureBean().getName());
		}
		catch (Exception e) {			
			getView().setValue("unitMeasure", "N/D");
		}
		
		try {

			if (mapItem.get("accountId").toString() != null) {
			 aid = (AccountInvoiceDetail) XPersistence.getManager()
						.createQuery("select  aid from AccountInvoice ai ,Account a, AccountInvoiceDetail aid "
								+ "WHERE  a.accountId = ai.accountId"
								+ " AND  ai.accountId=aid.accountInvoice.accountId AND  a.product.productId= '202'"
								+ " AND  aid.accountDetail.accountId =:accountDetail ORDER by ai.issueDate desc ")
						.setMaxResults(1).setParameter("accountDetail", mapItem.get("accountId").toString()).getSingleResult();
				getView().setValue("lastUnitPrice", aid.getUnitPrice());
			}
		

		} catch (Exception e) {
			getView().setValue("lastUnitPrice", new BigDecimal("0.00"));		
		}
		
	}

}
