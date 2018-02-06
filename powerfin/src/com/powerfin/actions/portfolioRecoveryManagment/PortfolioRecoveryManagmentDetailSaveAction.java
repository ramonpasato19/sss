package com.powerfin.actions.portfolioRecoveryManagment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxava.actions.CreateNewElementInCollectionAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.AccountLoanHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountOverdueBalance;

public class PortfolioRecoveryManagmentDetailSaveAction  extends  CreateNewElementInCollectionAction{

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		
		Map<String, String> accountLoan = (Map<String, String>) getView().getValue("accountLoan");
		if (accountLoan.get("accountId")==null  || accountLoan.get("accountId").isEmpty() ){
			throw new OperativeException("select_the_loan_to_manage");
		}
		super.execute();
		removeActions("Collection.saveAndStay"); 
		Account account = XPersistence.getManager().find(Account.class, accountLoan.get("accountId"));
		Integer number = getCollectionElementView().getCollectionSize()+1;
		
		
		getCollectionElementView().setValue("numberDetail",number);
		if (account!=null) {
			List<AccountOverdueBalance> accountOverdueBalances =  AccountLoanHelper.getOverdueBalances(account);
			BigDecimal capital = BigDecimal.ZERO;
			BigDecimal defaultInterest = BigDecimal.ZERO;
			BigDecimal interest = BigDecimal.ZERO;
			BigDecimal vehicleInsurance = BigDecimal.ZERO;
			BigDecimal mortgageInsurance = BigDecimal.ZERO;
			BigDecimal receivableFee = BigDecimal.ZERO;
			BigDecimal collectionFee = BigDecimal.ZERO;
			BigDecimal overdueBalances = BigDecimal.ZERO;			
			
			List<Date> dueDates = new ArrayList<Date>();
			Integer overdueDays=0;
			
			if (accountOverdueBalances!=null && !accountOverdueBalances.isEmpty()) {
				for (AccountOverdueBalance accountOverdueBalance:accountOverdueBalances) {
					capital = capital.add((accountOverdueBalance.getCapital()!=null)?accountOverdueBalance.getCapital():BigDecimal.ZERO);
					defaultInterest = defaultInterest.add((accountOverdueBalance.getDefaultInterest()!=null)?accountOverdueBalance.getDefaultInterest():BigDecimal.ZERO);
					interest = interest.add((accountOverdueBalance.getInterest()!=null)?accountOverdueBalance.getInterest():BigDecimal.ZERO);
					vehicleInsurance = vehicleInsurance.add((accountOverdueBalance.getInsurance()!=null)?accountOverdueBalance.getInsurance():BigDecimal.ZERO);
					mortgageInsurance = mortgageInsurance.add((accountOverdueBalance.getInsuranceMortgage()!=null)?accountOverdueBalance.getInsuranceMortgage():BigDecimal.ZERO);
					receivableFee = receivableFee.add((accountOverdueBalance.getReceivableFee()!=null)?accountOverdueBalance.getReceivableFee():BigDecimal.ZERO);
					collectionFee = collectionFee.add((accountOverdueBalance.getCollectionFee()!=null)?accountOverdueBalance.getCollectionFee():BigDecimal.ZERO);
					overdueBalances = overdueBalances.add((accountOverdueBalance.getTotal()!=null)?accountOverdueBalance.getTotal():BigDecimal.ZERO);
					overdueDays+=accountOverdueBalance.getOverdueDays();
					dueDates.add(accountOverdueBalance.getDueDate());
				}				
				getCollectionElementView().setValue("capital",capital);
				getCollectionElementView().setValue("defaultInterest",defaultInterest);
				getCollectionElementView().setValue("interest",interest);
				getCollectionElementView().setValue("vehicleInsurance",vehicleInsurance);
				getCollectionElementView().setValue("mortgageInsurance",mortgageInsurance);
				getCollectionElementView().setValue("mortgageInsurance",mortgageInsurance);
				getCollectionElementView().setValue("overdueQuotas",accountOverdueBalances.size());				
				getCollectionElementView().setValue("receivableFee",receivableFee);
				getCollectionElementView().setValue("overdueBalances",overdueBalances);
				getCollectionElementView().setValue("overdueFrom",getMinimumDueDate(dueDates));
			}
			
		}
		
	}
	/**
	 * Método que se encarga de recuperar la fecha de pago 
	 * más antigua para determinar desde cuando está vencido
	 * @param dueDates
	 * @return
	 */
	private Date getMinimumDueDate(List<Date> dueDates) {		
		for (int i=0;i<dueDates.size()-1;i++) {
			boolean isOlder = false;
			Date dateTmp = dueDates.get(i);
			for (int j=0;j<dueDates.size();j++) {
				if (dueDates.get(i).before(dateTmp)) {
					isOlder = true;
					break;
				}
			}
			if (!isOlder) {
				return dateTmp;
			}
		}
		return null;
	}
}
