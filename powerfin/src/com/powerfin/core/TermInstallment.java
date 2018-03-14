package com.powerfin.core;

import java.math.*;
import java.text.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.UtilApp;

public class TermInstallment {

	private static Log log = LogFactory.getLog(TermInstallment.class);
	
	public void execute(AccountTerm a, BigDecimal quota)
	{
		installment(a, quota);
	}
	
	private void installment(AccountTerm a, BigDecimal quota)
	{
		BigDecimal lastQuota = BigDecimal.ZERO;
		if (quota==null) 
			quota = this.calculateFixedQuota(a);
		log.info("*****************************Quota: " + quota);
		lastQuota = generatePaytable(a, quota, false, 6);
		log.info("*****************************LastQuota: " + lastQuota);
		log.info("*****************************DiffQuota: " + quota.subtract(lastQuota).doubleValue());
		
		if (a.getPaytableType().getPaytableTypeId().equals(AccountTermHelper.CAPITAL_INTEREST_PAYTABLE_TYPE_ID))
		{
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
		else if (a.getPaytableType().getPaytableTypeId().equals(AccountTermHelper.INTEREST_PAYTABLE_TYPE_ID))
		{
			quota.multiply(new BigDecimal(1)).setScale(2, RoundingMode.HALF_UP);
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Final Quota: " + quota);
			generatePaytable(a, quota, true, 2);
		}
	}
	
	private BigDecimal calculateFixedQuota(AccountTerm a)
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
			dayRate = dayRate.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
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
			periodRate = periodRate.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
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

	public BigDecimal generatePaytable(AccountTerm a, BigDecimal quota, boolean persistQuota, int scale)
	{
		Calendar disbursementDateCal = GregorianCalendar.getInstance();
		Calendar dueDateCal = GregorianCalendar.getInstance();
		//Calendar auxCal = GregorianCalendar.getInstance();
		Calendar previousDueDateCal = GregorianCalendar.getInstance();
		BigDecimal lastQuota = BigDecimal.ZERO;
		int quotaNumber;
		List<AccountPaytable> quotas = new ArrayList<AccountPaytable>();
		BigDecimal dayRate, interest, capitalReduced;
		BigDecimal capital = BigDecimal.ZERO;
		
		log.info("BEGIN INSTALLMENT ###################################");
		try {
			disbursementDateCal.setTime(a.getDisbursementDate());
			dueDateCal.setTime(a.getStartDatePayment());
			quotaNumber = 1;
			dayRate = a.getInterestRate().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
			dayRate = dayRate.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
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
				//auxCal.setTimeInMillis(dueDateCal.getTime().getTime() - previousDueDateCal.getTime().getTime());
				//aq.setProvisionDays(auxCal.get(Calendar.DAY_OF_YEAR));
				aq.setProvisionDays(UtilApp.getDaysCountBetweenDates(previousDueDateCal.getTime(), dueDateCal.getTime()));
				aq.setCapitalReduced(capitalReduced);
				interest = capitalReduced.multiply(dayRate);
				interest = interest.multiply(new BigDecimal(aq.getProvisionDays())).setScale(scale, RoundingMode.HALF_UP);
				aq.setInterest(interest);
				
				if (a.getPaytableType().getPaytableTypeId().equals(AccountTermHelper.CAPITAL_INTEREST_PAYTABLE_TYPE_ID))
				{
					capital = quota.subtract(interest).setScale(scale, RoundingMode.HALF_UP);
					
					if (capital.compareTo(BigDecimal.ZERO)<=0)
						capital = BigDecimal.ZERO;
					
					if (quotaNumber<=a.getQuotasNumber().intValue())
						aq.setCapital(capital);
					else
						aq.setCapital(capitalReduced);
					
					capitalReduced = capitalReduced.subtract(capital).setScale(scale, RoundingMode.HALF_UP);
				}
				else if (a.getPaytableType().getPaytableTypeId().equals(AccountTermHelper.INTEREST_PAYTABLE_TYPE_ID))
				{					
					if (quotaNumber<=a.getQuotasNumber().intValue())
						aq.setCapital(BigDecimal.ZERO);
					else
						aq.setCapital(capitalReduced);
				}
				
				lastQuota = aq.getTotalDividend();
				
				aq.setAccount(a.getAccount());
				aq.setAccountId(a.getAccount().getAccountId());
				quotas.add(aq);
			}
			log.info("END INSTALLMENT ###################################");
			
			if(persistQuota) 
				persistQuotas(a, quotas);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastQuota;
	}
	
	public void persistQuotas(AccountTerm a, List<AccountPaytable> quotas)
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
		
		log.info("UPDATE ACCOUNT TERM ############################");
		AccountTerm account = XPersistence.getManager().find(AccountTerm.class, a.getAccountId());
		account.setPeriod(period);
		XPersistence.getManager().merge(account);
		
	}
}
