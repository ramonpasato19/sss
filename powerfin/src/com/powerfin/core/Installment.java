package com.powerfin.core;

import java.math.*;
import java.text.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;

import com.powerfin.model.*;

public class Installment {

	private static Log log = LogFactory.getLog(Installment.class);
	
	public void execute(AccountLoan a, BigDecimal quota)
	{
		installment(a, quota);
	}
	
	private void installment(AccountLoan a, BigDecimal quota)
	{
		BigDecimal lastQuota = BigDecimal.ZERO;
		if (quota==null) 
			quota = this.calculateFixedQuota(a);
		log.info("*****************************Quota: " + quota);
		lastQuota = generatePaytable(a, quota, false, 6);
		log.info("*****************************LastQuota: " + lastQuota);
		log.info("*****************************DiffQuota: " + quota.subtract(lastQuota).doubleValue());
		if(quota.subtract(lastQuota).abs().compareTo(new BigDecimal(0.3))>0)
		{
			BigDecimal quotaAux = quota.subtract(lastQuota).divide(new BigDecimal(a.getQuotasNumber()), RoundingMode.HALF_UP).multiply(new BigDecimal(-1));
			log.info("*****************************AdjustQuota: " + quotaAux.doubleValue());
			quota = quota.add(quotaAux);
			installment(a, quota);
		}
		else
		{
			quota.multiply(new BigDecimal(1)).setScale(2, RoundingMode.HALF_UP);
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Final Quota: " + quota);
			generatePaytable(a, quota, true, 2);
		}
	}
	
	private BigDecimal calculateFixedQuota(AccountLoan a)
	{
		if (a.getInterestRate().compareTo(BigDecimal.ZERO)==0)
			return a.getAmount().divide(new BigDecimal(a.getQuotasNumber()), 2, RoundingMode.HALF_UP);
		
		Calendar disbursementDateCal = GregorianCalendar.getInstance();
		Calendar endDateCal = GregorianCalendar.getInstance();
		Calendar paymentDateCal = GregorianCalendar.getInstance();
		Calendar dueDateCal = GregorianCalendar.getInstance();
		Integer days;
		Integer monthlyFees;
		
		BigDecimal averageDays, periodRate, dayRate, pow, quotaA, quotaB, quotaC, quota;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		log.info("BEGIN CALCULATE QUOTA ###################################");
		try {
			
			monthlyFees = a.getQuotasNumber()*a.getFrecuency().getNumberMonths();
			
			disbursementDateCal.setTime(a.getDisbursementDate());
			endDateCal.setTime(a.getStartDatePayment());
			paymentDateCal.setTime(a.getStartDatePayment());
			dueDateCal.setTime(a.getStartDatePayment());

			dayRate = a.getInterestRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
			dayRate = dayRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);
			endDateCal.add(Calendar.MONTH, monthlyFees-a.getFrecuency().getNumberMonths());
			
			long difms=endDateCal.getTimeInMillis() - disbursementDateCal.getTimeInMillis();
			long difd=difms / (1000 * 60 * 60 * 24);
			
			days = new Long(difd).intValue();
			
			/*ajuste dias para aproximar la cuota*/
			
			Calendar previousPaymentDateCal = GregorianCalendar.getInstance();
			previousPaymentDateCal.setTime(a.getStartDatePayment());
			previousPaymentDateCal.add(Calendar.MONTH, a.getFrecuency().getNumberMonths()*-1);
			long provisionDayFirstQuotaMS=paymentDateCal.getTimeInMillis() - disbursementDateCal.getTimeInMillis();
			long provisionDayFirstQuotaD=provisionDayFirstQuotaMS / (1000 * 60 * 60 * 24);
			long adjustProvisionDayFirstQuotaMS=paymentDateCal.getTimeInMillis() - previousPaymentDateCal.getTimeInMillis();
			long adjustProvisionDayFirstQuotaD=adjustProvisionDayFirstQuotaMS / (1000 * 60 * 60 * 24);
			days += new Long(provisionDayFirstQuotaD).intValue();
			days -= new Long(adjustProvisionDayFirstQuotaD).intValue();
			
			/* ***************** */
			
			averageDays = new BigDecimal(days).divide(new BigDecimal(a.getQuotasNumber()), 10, RoundingMode.HALF_UP);
			periodRate = a.getInterestRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
			periodRate = periodRate.multiply(averageDays);
			periodRate = periodRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);
			pow = periodRate.add(BigDecimal.ONE);
			pow = pow.pow(a.getQuotasNumber(), new MathContext(10));
			
			quotaA = periodRate.multiply(pow);
			quotaB = pow.subtract(BigDecimal.ONE);
			quotaC = quotaA.divide(quotaB, 10, RoundingMode.HALF_UP);
			quota = a.getAmount().multiply(quotaC).setScale(2, RoundingMode.HALF_UP);

			log.info("disbursementDateCal: " + df.format(disbursementDateCal.getTime()));
			log.info("paymentDateCal: " + df.format(paymentDateCal.getTime()));
			log.info("endDateCal: " + df.format(endDateCal.getTime()));
			log.info("dueDateCal: " + df.format(dueDateCal.getTime()));
			
			log.info("period: " + a.getQuotasNumber());
			log.info("dayRate: " + dayRate);
			log.info("days: " + days);
			log.info("averageDays: " + averageDays);
			log.info("periodRate: " + periodRate);
			log.info("pow: " + pow);
			log.info("quotaA: " + quotaA);
			log.info("quotaB: " + quotaB);
			log.info("quotaC: " + quotaC);
			log.info("quota: " + quota);
			
			log.info("END CALCULATE QUOTA ###################################");
			return quota;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public BigDecimal generatePaytable(AccountLoan a, BigDecimal quota, boolean persistQuota, int scale)
	{
		Calendar disbursementDateCal = GregorianCalendar.getInstance();
		Calendar dueDateCal = GregorianCalendar.getInstance();
		Calendar auxCal = GregorianCalendar.getInstance();
		Calendar previousDueDateCal = GregorianCalendar.getInstance();
		BigDecimal lastQuota = BigDecimal.ZERO;
		int quotaNumber;
		List<AccountPaytable> quotas = new ArrayList<AccountPaytable>();
		BigDecimal dayRate, capital, interest, capitalReduced, insuranceMortgageDividend, insurance;
		
		BigDecimal insuranceAux = BigDecimal.ZERO;
		log.info("BEGIN INSTALLMENT ###################################");
		try {
			disbursementDateCal.setTime(a.getDisbursementDate());
			dueDateCal.setTime(a.getStartDatePayment());
			quotaNumber = 1;
			dayRate = a.getInterestRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
			dayRate = dayRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);
			capitalReduced = a.getAmount();
			for (int i=0; i<a.getQuotasNumber(); i++)
			{
				AccountPaytable aq = new AccountPaytable();
				if (i>0)
				{
					previousDueDateCal.setTime(dueDateCal.getTime());
					dueDateCal.add(Calendar.MONTH, a.getFrecuency().getNumberMonths());
					if (a.getPaymentDay() == 31)
						dueDateCal.set(Calendar.DAY_OF_MONTH, dueDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
					else if (a.getPaymentDay() > dueDateCal.getActualMaximum(Calendar.DAY_OF_MONTH))
						dueDateCal.set(Calendar.DAY_OF_MONTH, dueDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
					else
						dueDateCal.set(Calendar.DAY_OF_MONTH, a.getPaymentDay());
				}
				else
					previousDueDateCal.setTime(disbursementDateCal.getTime());
				
				aq.setSubaccount(quotaNumber);
				quotaNumber++;
				aq.setDueDate(dueDateCal.getTime());
				auxCal.setTimeInMillis(dueDateCal.getTime().getTime() - previousDueDateCal.getTime().getTime());
				aq.setProvisionDays(auxCal.get(Calendar.DAY_OF_YEAR));
				aq.setCapitalReduced(capitalReduced);
				interest = capitalReduced.multiply(dayRate);
				interest = interest.multiply(new BigDecimal(aq.getProvisionDays())).setScale(scale, RoundingMode.HALF_UP);
				aq.setInterest(interest);
				capital = quota.subtract(interest).setScale(scale, RoundingMode.HALF_UP);
				if (capital.compareTo(BigDecimal.ZERO)<=0)
					capital = BigDecimal.ZERO;
				if (quotaNumber<=a.getQuotasNumber().intValue())
					aq.setCapital(capital);
				else
					aq.setCapital(capitalReduced);
				
				/*desgravamen***/
				if (a.getMortgageInsurer()!=null)
				{
					aq.setInsuranceMortgage(BigDecimal.ZERO);
					insuranceMortgageDividend = a.getAmount();
					
					if (a.getMortgageInsurer().getInsuranceMortgageAmortization().equals("DISTRIB"))
						insuranceMortgageDividend = capitalReduced.add(interest);
					
					insuranceMortgageDividend = insuranceMortgageDividend
							.multiply(a.getMortgageInsurer().getMortgageRate())
							.setScale(scale, RoundingMode.HALF_UP);
					
						
					aq.setInsuranceMortgage(insuranceMortgageDividend);
				}
				/*fin desgravamen***/
				
				/*seguro***/
				aq.setInsurance(BigDecimal.ZERO);
				if (a.getInsuranceAmount()!=null)
				{
					if (a.getInsuranceAmount().compareTo(insuranceAux)>0)
					{
						insurance = a.getInsuranceAmount().divide(new BigDecimal(a.getInsuranceQuotasNumber()), scale, RoundingMode.HALF_UP);
						if (i==a.getInsuranceQuotasNumber()-1)
							insurance = a.getInsuranceAmount().subtract(insuranceAux);
						aq.setInsurance(insurance);
						insuranceAux = insuranceAux.add(insurance);
					}
				}
				/*fin seguro***/
				
				capitalReduced = capitalReduced.subtract(capital).setScale(scale, RoundingMode.HALF_UP);
				lastQuota = aq.getTotalDividend();
				
				aq.setAccount(a.getAccount());
				aq.setAccountId(a.getAccount().getAccountId());
				quotas.add(aq);
			}
			log.info("END INSTALLMENT ###################################");
			
			if(persistQuota) 
			{
				if (a.getMortgageInsurer()!=null && 
						a.getMortgageInsurer().getInsuranceMortgageAmortization().equals("DISTRIB"))
					redistributeInsurance(a, quotas);
				persistQuotas(a, quotas);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastQuota;
	}
	
	public void redistributeInsurance(AccountLoan a, List<AccountPaytable> quotas)
	{
		BigDecimal insuranceAux=BigDecimal.ZERO;
		BigDecimal insuranceOriginal=BigDecimal.ZERO;
		BigDecimal insuranceFinal=BigDecimal.ZERO;
		List<BigDecimal> annualInsuranceRealValue = new ArrayList<BigDecimal>();
		List<BigDecimal> firstInsuranceRealValueOfYear = new ArrayList<BigDecimal>();
		BigDecimal annualInsuranceNewValue=BigDecimal.ZERO;
		int monthsByPeriod = 6;//a.getQuotasNumber().intValue()/2;
		if(a.getQuotasNumber()==24)
			monthsByPeriod = 12;
		else if (a.getQuotasNumber()==36)
			monthsByPeriod = 18;
		else if (a.getQuotasNumber()==48)
			monthsByPeriod = 12;
		else if (a.getQuotasNumber()==60)
			monthsByPeriod = 18;
		
		int years = a.getQuotasNumber().intValue()/monthsByPeriod;
		int months = monthsByPeriod;
		int monthsAux = monthsByPeriod;
		log.info("BEGIN redistribute ----------------------------------------");
		log.info("years: "+years);
		for (AccountPaytable q:quotas)
		{
			insuranceOriginal = insuranceOriginal.add(q.getInsuranceMortgage());
		}
		for (int i=0; i<a.getQuotasNumber().intValue(); i++)
		{
			AccountPaytable q = quotas.get(i);
			if (i<months)
			{
				insuranceAux =insuranceAux.add(q.getInsuranceMortgage());
			}
			if (i==months-1)
			{
				annualInsuranceRealValue.add(insuranceAux);
				insuranceAux=BigDecimal.ZERO;
				months=months+monthsByPeriod;
			}
			if (i==0)
				firstInsuranceRealValueOfYear.add(q.getInsuranceMortgage());
			if (i==monthsAux)
			{
				firstInsuranceRealValueOfYear.add(q.getInsuranceMortgage());
				monthsAux=monthsAux+monthsByPeriod;
			}
		}
		log.info("annualInsuranceRealValue: "+annualInsuranceRealValue);
		log.info("firstInsuranceRealValueOfYear: "+firstInsuranceRealValueOfYear);
		
		int quotaMin=0;
		int quotaMax=monthsByPeriod;
		
		for (int i=0; i<years; i++)
		{
			log.info("ANIO "+i+": *************");
			log.info("quotaMin: "+quotaMin);
			log.info("quotaMax: "+quotaMax);
			if(i%2==0)
			{
				log.info("periodo par: YES");
				for (int j=quotaMin; j<quotaMax; j++)
				{
					((AccountPaytable)quotas.get(j)).setInsuranceMortgage(firstInsuranceRealValueOfYear.get(i));
					annualInsuranceNewValue = annualInsuranceNewValue.add(firstInsuranceRealValueOfYear.get(i));
				}
				log.info("firstInsuranceRealValueOfYear: "+firstInsuranceRealValueOfYear.get(i));
				log.info("annualInsuranceNewValue: "+annualInsuranceNewValue);
				
			}
			if(i%2!=0)
			{
				log.info("periodo IMpar: YES");
				insuranceAux = annualInsuranceNewValue.subtract(annualInsuranceRealValue.get(i-1));
				log.info("diference: "+insuranceAux);
				annualInsuranceNewValue=BigDecimal.ZERO;
				insuranceAux = annualInsuranceRealValue.get(i).subtract(insuranceAux);
				log.info("new Anual value: "+insuranceAux);
				insuranceAux = insuranceAux.divide(new BigDecimal(monthsByPeriod),2, RoundingMode.HALF_UP);
				log.info("new Anual value / monthsByPeriod: "+insuranceAux);
				for (int j=quotaMin; j<quotaMax; j++)
				{
					((AccountPaytable)quotas.get(j)).setInsuranceMortgage(insuranceAux);
				}
			}
			quotaMin=quotaMax;
			quotaMax+=monthsByPeriod;
		}
		
		for (int i=0; i<a.getQuotasNumber().intValue()-1; i++)
		{
			AccountPaytable q = quotas.get(i);
			insuranceFinal = insuranceFinal.add(q.getInsuranceMortgage());
		}
		
		//adjust last quota
		((AccountPaytable)quotas.get(a.getQuotasNumber()-1)).setInsuranceMortgage(insuranceOriginal.subtract(insuranceFinal));
		
		log.info("END redistribute ----------------------------------------");
	}
	
	public void persistQuotas(AccountLoan a, List<AccountPaytable> quotas)
	{
		log.info("DELETE OLD QUOTAS /////////////////////////");
		XPersistence.getManager().createQuery("DELETE FROM AccountPaytable a "
				+ "WHERE a.account.accountId = :accountId")
				.setParameter("accountId", a.getAccountId())
				.executeUpdate();
		log.info("PERSIST QUOTAS ############################");
		Integer period = 0;
		BigDecimal fixedQuota = BigDecimal.ZERO;
		for (AccountPaytable aq:quotas)
		{
			if (aq.getSubaccount() == 1)
			{
				fixedQuota = fixedQuota.add(aq.getCapital());
				fixedQuota = fixedQuota.add(aq.getInterest());
			}
			period=period + aq.getProvisionDays();
			XPersistence.getManager().persist(aq);
		}
		
		log.info("UPDATE ACCOUNT LOAN ############################");
		AccountLoan loan = XPersistence.getManager().find(AccountLoan.class, a.getAccountId());
		loan.setPeriod(period);
		loan.setFixedQuota(fixedQuota);
		XPersistence.getManager().merge(loan);
		
	}
}
