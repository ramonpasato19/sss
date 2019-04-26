package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.dto.*;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;
import com.powerfin.util.*;

public class FinancialHelper {

	public final static String DEFAULT_FIANCIAL_STATUS = "N";
	public final static String REVERSE_FIANCIAL_STATUS = "R";
	
	public static Date initDate;
	public static Date partialDate;
	
	@SuppressWarnings("unchecked")
	public static void saveFinancial(Transaction t) throws Exception {
		initDate = new Date();
		System.out.println("Begin Save Financial ------------------------- "+UtilApp.dateToString(initDate, "yyyy-MM-dd HH:mm:ss"));
		System.out.println("Transaction: "+t.getTransactionId());
		int line = 0;
		Financial f = new Financial();
		f.setAccountingDate(CompanyHelper.getCurrentAccountingDate());
		f.setFinancialStatus(FinancialHelper.getDefaultFinancialStatus());
		f.setRemark(t.getRemark());
		f.setVoucher(t.getVoucher());
		f.setOrigenUnityId(t.getOrigenUnity());
		f.setTransaction(t);

		List<FinancialCategoryDTO> financialCategories = new ArrayList<FinancialCategoryDTO>();

		List<TransactionAccount> transactionAccounts = XPersistence.getManager()
				.createQuery("SELECT ta FROM TransactionAccount ta "
					+ "WHERE ta.transaction.transactionId = :transactionId "
					+ "ORDER BY ta.registrationDate ")
				.setParameter("transactionId", t.getTransactionId())
				.getResultList();
		
		if (transactionAccounts==null || transactionAccounts.isEmpty())
			throw new OperativeException("unable_to_process_transaction_without_detail_of_accouts");
		
		List<Movement> movements = new ArrayList<Movement>();
		
		for (TransactionAccount ta : transactionAccounts) {
			
			if (ta.getQuantity()==null)
				ta.setQuantity(BigDecimal.ZERO);
			
			if (ta.getValue()==null)
				ta.setValue(BigDecimal.ZERO);
			
			if (ta.getValue().compareTo(BigDecimal.ZERO)==0 && ta.getQuantity().compareTo(BigDecimal.ZERO)==0)
				continue;
			
			String bookAccountParametrized = BookAccountHelper.getBookAccountParametrized(
					ta.getAccount(), ta.getCategory(), ta.getSubaccount());
			
			BookAccount bookAccount = BookAccountHelper.getBookAccount(bookAccountParametrized);
			
			if (bookAccount!=null)
			{
				line += 1;
				Movement m = new Movement();
				m.setLine(line);
				m.setAccount(ta.getAccount());
				m.setBookAccount(bookAccount);
				m.setCategory(ta.getCategory());
				m.setDebitOrCredit(ta.getDebitOrCredit());
				m.setFinancial(f);
				m.setSubaccount(ta.getSubaccount());
				m.setQuantity(ta.getQuantity());
				m.setUnity(ta.getUnity());
				m.setRemark(ta.getRemark());
				m.setExchangeRate(ExchangeRateHelper.getExchangeRate(ta.getAccount().getCurrency(), t.getAccountingDate()));
				m.setValue(ta.getValue().abs());
				m.setOfficialValue(UtilApp.valueToOfficialValue(ta.getValue(), m.getExchangeRate()));
				m.setBranch(ta.getBranch());
				
				if (ta.getOfficialValue().equals(Types.YesNoIntegerType.YES))
				{
					m.setOfficialValue(ta.getValue().abs());
					m.setValue(BigDecimal.ZERO);
				}

				if (!bookAccount.getGroupAccount().getDebtorOrCreditor().equals(ta.getDebitOrCredit())) {
					m.setValue(m.getValue().negate());
					m.setQuantity(m.getQuantity().negate());
					m.setOfficialValue(m.getOfficialValue().negate());
				}
				
				if (m.getValue().add(m.getOfficialValue()).compareTo(BigDecimal.ZERO)!=0 || m.getQuantity().compareTo(BigDecimal.ZERO)!=0)
				{
					m.setTransactionAccount(ta);
					movements.add(m);
					addFinancialCategory(financialCategories, m, ta.getUpdateBalance(), ta.getOfficialValue(), bookAccount.getAllowCurrencyAdjustment(), ta.getDueDate());
				}
			}
			else
				throw new InternalException("book_account_not_found",
						ta.getAccount().getAccountId(), ta.getSubaccount(), ta.getCategory().getCategoryId(), bookAccountParametrized, ta.getValue());
		}
		
		if (movements.isEmpty())
			throw new OperativeException("unable_to_process_financial_without_movements", t.getVoucher());
		
		validateAccountingEquation(movements);
		
		XPersistence.getManager().persist(f);
		System.out.println("Persist Financial: "+f.getFinancialId());

		for (Movement m : movements) {
			m.setFinancial(f);
			XPersistence.getManager().persist(m);
			System.out.println(m.toString());
			if (m.getTransactionAccount().getUpdateBalance().equals(YesNoIntegerType.YES))
				updateBalance(m);
		}
		
		for (FinancialCategoryDTO financialCategory : financialCategories) 
			if (financialCategory.getUpdateBalance().equals(YesNoIntegerType.YES))
				validateFinalBalance(financialCategory);
	
		System.out.println("End Save Financial ------------------------- "+UtilApp.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")+", time: "+UtilApp.getSecondsCountBetweenDates(initDate, new Date())+"\n");
	}

	private static void validateFinalBalance(FinancialCategoryDTO dto) throws Exception
	{
		BigDecimal stock;
		BigDecimal balance;
		Balance activeBalance = getActiveBalance(dto.getAccountingDate(), dto.getAccount().getAccountId(), dto.getSubaccount(), 
				dto.getCategory().getCategoryId(), dto.getBranch().getBranchId());
		
		stock =  activeBalance.getStock();
		balance = activeBalance.getBalance();
		
		//Validate NegativeBalance
		if (!CategoryHelper.getAllowsNegativeBalance(dto.getAccount(), dto.getCategory()))
		{
			if (stock.compareTo(BigDecimal.ZERO)<0)
				throw new OperativeException("the_account_stock_can_not_be_negative",
						dto.getAccount().getAccountId(),
						dto.getSubaccount(),
						dto.getCategory().getCategoryId(),
						dto.getBranch().getBranchId(),
						activeBalance.getStock());
			
			if (balance.compareTo(BigDecimal.ZERO)<0)
				throw new OperativeException("the_account_balance_can_not_be_negative",
						dto.getAccount().getAccountId(),
						dto.getSubaccount(),
						dto.getCategory().getCategoryId(),
						dto.getBranch().getBranchId(),
						activeBalance.getBalance());
		}
		
		//Expire balance
		if (balance.compareTo(BigDecimal.ZERO)==0)
		{
			if (dto.getCategory().getExpiresZeroBalance().equals(Types.YesNoIntegerType.YES))
			{
				activeBalance.setToDate(CompanyHelper.getCurrentAccountingDate());
				XPersistence.getManager().merge(activeBalance);
			}
		}

	}
	
	private static void updateBalance(Movement m)
			throws Exception {

		Date date = m.getAccountingDate();
		boolean persistNewBalance = true;

		Balance newBalance = new Balance();
		newBalance.setAccount(m.getAccount());
		newBalance.setAccountingDate(date);
		newBalance.setFromDate(date);
		newBalance.setToDate(UtilApp.DEFAULT_EXPIRY_DATE);
		newBalance.setBookAccount(m.getBookAccount());
		newBalance.setCategory(m.getCategory());
		newBalance.setCurrency(m.getAccount().getCurrency());
		newBalance.setSubaccount(m.getSubaccount());
		newBalance.setBalance(m.getValue());
		newBalance.setOfficialBalance(m.getOfficialValue());
		newBalance.setDueDate(m.getTransactionAccount().getDueDate());
		newBalance.setStock(m.getQuantity());
		newBalance.setBranch(m.getBranch());
		
		if (m.getBookAccount().getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.YES))
			newBalance.setOfficialBalance(BigDecimal.ZERO);
		
		Balance activeBalance = getActiveBalance(date, m.getAccount().getAccountId(), m.getSubaccount(), 
				m.getCategory().getCategoryId(), m.getBranch().getBranchId());
		
		if (activeBalance != null )
		{
			if (activeBalance.getFromDate().compareTo(date) == 0)
			{
				activeBalance.setBalance(activeBalance.getBalance().add(m.getValue()));
				activeBalance.setStock(activeBalance.getStock().add(m.getQuantity()!=null?m.getQuantity():BigDecimal.ZERO));
				
				if (m.getBookAccount().getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.NO))
					activeBalance.setOfficialBalance(activeBalance.getOfficialBalance().add(m.getOfficialValue()));
				else
					activeBalance.setOfficialBalance(BigDecimal.ZERO);
				
				persistNewBalance = false;
				
			}
			else if (activeBalance.getFromDate().compareTo(date) < 0)
			{
				newBalance.setDueDate(activeBalance.getDueDate());
				newBalance.setBalance(newBalance.getBalance().add(activeBalance.getBalance()));
				newBalance.setStock(newBalance.getStock().add(activeBalance.getStock()!=null?activeBalance.getStock():BigDecimal.ZERO));
				
				if (m.getBookAccount().getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.NO))
					newBalance.setOfficialBalance(newBalance.getOfficialBalance().add(activeBalance.getOfficialBalance()));
				else
					newBalance.setOfficialBalance(BigDecimal.ZERO);
				
				Calendar oldToDate = Calendar.getInstance();
				oldToDate.setTime(date);
				oldToDate.add(Calendar.DAY_OF_YEAR, -1);
				activeBalance.setToDate(oldToDate.getTime());
				
				XPersistence.getManager().merge(activeBalance);
				XPersistence.getManager().flush();
				
				persistNewBalance = true;
			}
			else
			{
				throw new InternalException("exist_future_balances");
			}
		}

		if (persistNewBalance)
		{
			XPersistence.getManager().persist(newBalance);
			System.out.println("---Persist New "+newBalance.toString());
		}
		else
		{
			XPersistence.getManager().merge(activeBalance);
			System.out.println("---Update Active "+activeBalance.toString());
		}

	}

	@SuppressWarnings("unchecked")
	private static Balance getActiveBalance(Date accountingDate, String accountId, int subaccount, String categoryId, int branchId
			) throws Exception {

		List<Balance> balances = XPersistence
				.getManager()
				.createQuery(
						"SELECT o FROM Balance o "
								+ "WHERE :date between fromDate AND toDate "
								+ "AND o.account.accountId = :account "
								+ "AND o.subaccount = :subaccount "
								+ "AND o.category.categoryId = :category "
								+ "AND o.branch.branchId = :branch ")
				.setParameter("date", accountingDate)
				.setParameter("account", accountId)
				.setParameter("subaccount", subaccount)
				.setParameter("category", categoryId)
				.setParameter("branch", branchId)
				.getResultList();
		if (balances != null && !balances.isEmpty() && balances.size() > 0 && ((Balance) balances.get(0)).getBalanceId() != null) {
			return (Balance) balances.get(0);
		} else {
			return null;
		}
	}
	
	private static void addFinancialCategory(
			List<FinancialCategoryDTO> financialCategories, 
			Movement m, 
			Types.YesNoIntegerType updateBalance, 
			Types.YesNoIntegerType isOfficialValue, 
			Types.YesNoIntegerType allowCurrencyAdjustment,
			Date dueDate) {
		boolean addFinancialCategory = true;

		for (FinancialCategoryDTO financialCategory : financialCategories) 
		{
			if (financialCategory.getAccount().getAccountId().equals(m.getAccount().getAccountId()) &&
					financialCategory.getSubaccount()==m.getSubaccount() &&
					financialCategory.getCategory().getCategoryId().equals(m.getCategory().getCategoryId()) &&
					financialCategory.getBranch().getBranchId().equals(m.getBranch().getBranchId())
					)
			{
				addFinancialCategory = false;
				break;
			}
		}
		if (addFinancialCategory) 
			financialCategories.add( new FinancialCategoryDTO(
					m.getAccount(),
					m.getSubaccount(), 
					m.getCategory(), 
					m.getBookAccount(),
					m.getFinancial().getAccountingDate(),
					m.getExchangeRate(), 
					updateBalance, 
					isOfficialValue, 
					allowCurrencyAdjustment, 
					dueDate, 
					m.getBranch()));
	}

	private static void validateAccountingEquation(List<Movement> movements)
			throws Exception {
		BigDecimal debits = BigDecimal.ZERO;
		BigDecimal credits = BigDecimal.ZERO;

		BigDecimal oDebits = BigDecimal.ZERO;
		BigDecimal oCredits = BigDecimal.ZERO;
		
		System.out.println("Validate Accounting Equation...");
		
		for(Movement m : movements)
		{
			if (m.getDebitOrCredit().equals(DebitOrCredit.DEBIT))
				debits = debits.add(m.getOfficialValue().abs());
			else
				credits = credits.add(m.getOfficialValue().abs());
		}
		System.out.println(new StringBuffer("OD:").append(debits).append("|OC:").append(credits));
		if (credits.compareTo(debits)!=0)
		{	
			for(Movement m : movements)
			{
				if (m.getDebitOrCredit().equals(DebitOrCredit.DEBIT))
					oDebits = oDebits.add(m.getValue().abs());
				else
					oCredits = oCredits.add(m.getValue().abs());
			}
			System.out.println(new StringBuffer("D:").append(oDebits).append("|C:").append(oCredits));
			if (oCredits.compareTo(oDebits)!=0)
				throw new InternalException("transaction_unbalanced", debits, credits, oDebits, oCredits);
		}
	}

	public static FinancialStatus getDefaultFinancialStatus() throws Exception {
		return XPersistence.getManager().find(FinancialStatus.class,
				FinancialHelper.DEFAULT_FIANCIAL_STATUS);
	}

	public static FinancialStatus getReverseFinancialStatus() throws Exception {
		return XPersistence.getManager().find(FinancialStatus.class,
				FinancialHelper.REVERSE_FIANCIAL_STATUS);
	}
}
