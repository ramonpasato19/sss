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
	
	@SuppressWarnings("unchecked")
	public static void saveFinancial(Transaction t) throws Exception {
		System.out.println("Begin Save Financial -------------------------");
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
					ta.getAccount(), ta.getCategory());
			
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
		System.out.println("Financial: "+f.getFinancialId());

		for (Movement m : movements) {
			m.setFinancial(f);
			XPersistence.getManager().persist(m);
			System.out.println(m.toString());
		}
		for (FinancialCategoryDTO financialCategory : financialCategories) 
			if (financialCategory.getUpdateBalance().equals(YesNoIntegerType.YES))
				updateBalance(financialCategory);

		System.out.println("End Save Financial -------------------------");
	}

	@SuppressWarnings("unused")
	private static void validateNegativeBalance (
			FinancialCategoryDTO financialCategory) throws Exception {
		
		if (CategoryHelper.getAllowsNegativeBalance(financialCategory.getAccount(), financialCategory.getCategory()))
			return;
		
		BigDecimal balance = BalanceHelper.getBalance(financialCategory.getAccount(), financialCategory.getSubaccount(), financialCategory.getCategory());
		if (balance==null)
			throw new InternalException("null_balance_for_validate_balances",
					financialCategory.getAccount().getAccountId(),
					financialCategory.getSubaccount(),
					financialCategory.getCategory().getCategoryId());

		if (balance.compareTo(BigDecimal.ZERO)<0)
			throw new OperativeException("the_account_balance_can_not_be_negative",
					financialCategory.getAccount().getAccountId(),
					financialCategory.getSubaccount(),
					financialCategory.getCategory().getCategoryId(),
					balance);
	}

	private static void updateBalance(FinancialCategoryDTO financialCategory)
			throws Exception {

		Calendar start = Calendar.getInstance();
		start.setTime(financialCategory.getAccountingDate());
		Calendar end = Calendar.getInstance();
		end.setTime(CompanyHelper.getCurrentAccountingDate());
		end.add(Calendar.DATE, 1);

		for (Date date = start.getTime(); start.before(end); start.add(
				Calendar.DATE, 1), date = start.getTime()) {
			System.out.println(new StringBuffer("Updating Balance: ") 
					.append(financialCategory.getAccount().getAccountId())
					.append("|")
					.append(financialCategory.getSubaccount())
					.append("|")
					.append(financialCategory.getCategory().getCategoryId())
					.append("|")
					.append(UtilApp.dateToString(date)));
			
			fillValues(financialCategory, date);
			
			Balance newBalance = new Balance();
			newBalance.setAccount(financialCategory.getAccount());
			newBalance.setAccountingDate(date);
			newBalance.setFromDate(date);
			newBalance.setToDate(UtilApp.DEFAULT_EXPIRY_DATE);
			newBalance.setBookAccount(financialCategory.getBookAccount());
			newBalance.setCategory(financialCategory.getCategory());
			newBalance.setCurrency(financialCategory.getAccount().getCurrency());
			newBalance.setSubaccount(financialCategory.getSubaccount());
			newBalance.setBalance(financialCategory.getValue());
			newBalance.setOfficialBalance(financialCategory.getOfficialValue());
			newBalance.setDueDate(financialCategory.getDueDate());
			newBalance.setStock(financialCategory.getStock());
			
			if (financialCategory.getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.YES))
				newBalance.setOfficialBalance(BigDecimal.ZERO);			
			
			Balance oldBalanceOnDate = getOldBalanceOnDate(financialCategory,date);
			
			int balancesDeleted = XPersistence
					.getManager()
					.createQuery(
							"DELETE FROM Balance o "
									+ "WHERE o.account = :account "
									+ "AND o.subaccount = :subaccount "
									+ "AND o.category = :category "
									+ "AND o.fromDate >= :fromDate")
					.setParameter("fromDate", date)
					.setParameter("account", financialCategory.getAccount())
					.setParameter("subaccount",
							financialCategory.getSubaccount())
					.setParameter("category", financialCategory.getCategory())
					.executeUpdate();
			
			System.out.println("Future Balances removed: "+balancesDeleted);
			
			if (oldBalanceOnDate != null) {
				Calendar oldToDate = Calendar.getInstance();
				oldToDate.setTime(date);
				oldToDate.add(Calendar.DAY_OF_YEAR, -1);
				oldBalanceOnDate.setToDate(oldToDate.getTime());
				XPersistence.getManager().merge(oldBalanceOnDate);
				System.out.println(new StringBuffer("Old Balance expired: ")
						.append(oldBalanceOnDate.getAccount().getAccountId())
						.append("|")
						.append(oldBalanceOnDate.getSubaccount())
						.append("|")
						.append(oldBalanceOnDate.getCategory().getCategoryId())
						.append("|")
						.append(oldBalanceOnDate.getBalance())
						.append("|")
						.append(oldBalanceOnDate.getOfficialBalance())
						.append("|")
						.append(oldBalanceOnDate.getStock()));
				
				newBalance.setDueDate(oldBalanceOnDate.getDueDate());
				newBalance.setBalance(newBalance.getBalance().add(oldBalanceOnDate.getBalance()));
				newBalance.setStock(newBalance.getStock().add(oldBalanceOnDate.getStock()!=null?oldBalanceOnDate.getStock():BigDecimal.ZERO));
				
				if (financialCategory.getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.NO))
					newBalance.setOfficialBalance(newBalance.getOfficialBalance().add(oldBalanceOnDate.getOfficialBalance()));
				else
					newBalance.setOfficialBalance(BigDecimal.ZERO);
			}
			
			//Expire balance
			if (financialCategory.getCategory().getAllowsNegativeBalance().equals(Types.YesNoIntegerType.NO))
				if (financialCategory.getCategory().getExpiresZeroBalance().equals(Types.YesNoIntegerType.YES))
					if (newBalance.getBalance().compareTo(BigDecimal.ZERO)==0)
						newBalance.setToDate(CompanyHelper.getCurrentAccountingDate());
			
			//Validate NegativeBalance
			if (!CategoryHelper.getAllowsNegativeBalance(financialCategory.getAccount(), financialCategory.getCategory()))
			{			
				if (newBalance.getBalance().compareTo(BigDecimal.ZERO)<0)
					throw new OperativeException("the_account_balance_can_not_be_negative",
							financialCategory.getAccount().getAccountId(),
							financialCategory.getSubaccount(),
							financialCategory.getCategory().getCategoryId(),
							newBalance.getBalance());
				
				if (newBalance.getStock().compareTo(BigDecimal.ZERO)<0)
					throw new OperativeException("the_account_stock_can_not_be_negative",
							financialCategory.getAccount().getAccountId(),
							financialCategory.getSubaccount(),
							financialCategory.getCategory().getCategoryId(),
							newBalance.getStock());
			}
			
			XPersistence.getManager().persist(newBalance);
			
			if (financialCategory.getAllowCurrencyAdjustment().equals(Types.YesNoIntegerType.NO))
				System.out.println(new StringBuffer("Persist New Balance: ") 
						.append(newBalance.getAccount().getAccountId())
						.append("|")
						.append(newBalance.getSubaccount())
						.append("|")
						.append(newBalance.getCategory().getCategoryId())
						.append("|")
						.append(newBalance.getBalance())
						.append("|")
						.append(newBalance.getOfficialBalance())
						.append("|")
						.append(newBalance.getStock())
						.append("|")
						.append(newBalance.getBalanceId())
						.append("|")
						.append(UtilApp.dateToString(newBalance.getToDate())));
			else
				System.out.println(new StringBuffer("Persist New Balance: ")
						.append(newBalance.getAccount().getAccountId())
						.append("|")
						.append(newBalance.getSubaccount())
						.append("|")
						.append(newBalance.getCategory().getCategoryId())
						.append("|")
						.append(newBalance.getBalance())
						.append("|")
						.append(UtilApp.valueToOfficialValue(newBalance.getBalance(), financialCategory.getExchangeRate()))
						.append("|")
						.append(newBalance.getStock())
						.append("|")
						.append(newBalance.getBalanceId())
						.append("|")
						.append(UtilApp.dateToString(newBalance.getToDate())));
		}

	}

	@SuppressWarnings("unchecked")
	private static void fillValues(FinancialCategoryDTO financialCategory,
			Date accountingDate) throws Exception {
		financialCategory.setValue(BigDecimal.ZERO);
		financialCategory.setOfficialValue(BigDecimal.ZERO);
		financialCategory.setStock(BigDecimal.ZERO);

		String schema = XPersistence.getDefaultSchema().toLowerCase();
		
		String query = "SELECT "
		 		+ "SUM(COALESCE(o.value,0)) as value, "
		 		+ "SUM(COALESCE(o.official_value,0)) as official_value, "
		 		+ "SUM(COALESCE(o.quantity,0)) as quantity "
		 		+ "FROM "+schema+".movement o, "+schema+".financial f "
				+ "WHERE f.accounting_date = :accountingDate "
				+ "AND o.account_id = :account "
				+ "AND o.subaccount = :subaccount "
				+ "AND o.category_id = :category "
				+ "AND o.financial_id = f.financial_id";
		
		 List<Object[]> acumulatedMovements = XPersistence.getManager()
				 .createNativeQuery(query)
				 .setParameter("accountingDate", accountingDate)
				 .setParameter("account", financialCategory.getAccount().getAccountId())
				 .setParameter("subaccount", financialCategory.getSubaccount())
				 .setParameter("category", financialCategory.getCategory().getCategoryId())
				 .getResultList();
		
		if (acumulatedMovements != null && !acumulatedMovements.isEmpty()) {
			
			Object[] acumulatedValues = (Object[])acumulatedMovements.get(0);
			if (acumulatedValues!=null) 
			{
				System.out.println(new StringBuffer("Add Movements of Day: ") 
						.append(acumulatedValues[0])
						.append("|")
						.append(acumulatedValues[1])
						.append("|")
						.append(acumulatedValues[2]));
				financialCategory.setValue(financialCategory.getValue().add((BigDecimal) acumulatedValues[0]));
				financialCategory.setOfficialValue(financialCategory.getOfficialValue().add((BigDecimal) acumulatedValues[1]));
				financialCategory.setStock(financialCategory.getStock().add((BigDecimal) acumulatedValues[2]));
				
			}
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static Balance getBalanceOnDate(
			FinancialCategoryDTO financialCategory, Date fromDate)
			throws Exception {
		List<Balance> balances = XPersistence
				.getManager()
				.createQuery(
						"SELECT o FROM Balance o "
								+ "WHERE o.account = :account "
								+ "AND o.subaccount = :subaccount "
								+ "AND o.category = :category "
								+ "AND o.fromDate = :fromDate")
				.setParameter("fromDate", fromDate)
				.setParameter("account", financialCategory.getAccount())
				.setParameter("subaccount", financialCategory.getSubaccount())
				.setParameter("category", financialCategory.getCategory())
				.getResultList();
		if (balances != null && !balances.isEmpty() && balances.size() > 0) {
			return (Balance) balances.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static Balance getOldBalanceOnDate(
			FinancialCategoryDTO financialCategory, Date date) throws Exception {
		Calendar oldToDate = Calendar.getInstance();
		oldToDate.setTime(date);
		oldToDate.add(Calendar.DAY_OF_YEAR, -1);

		List<Balance> balances = XPersistence
				.getManager()
				.createQuery(
						"SELECT o FROM Balance o "
								+ "WHERE :date between fromDate AND toDate "
								+ "AND o.account = :account "
								+ "AND o.subaccount = :subaccount "
								+ "AND o.category = :category ")
				.setParameter("date", oldToDate.getTime())
				.setParameter("account", financialCategory.getAccount())
				.setParameter("subaccount", financialCategory.getSubaccount())
				.setParameter("category", financialCategory.getCategory())
				.getResultList();
		if (balances != null && !balances.isEmpty() && balances.size() > 0) {
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
					financialCategory.getCategory().getCategoryId().equals(m.getCategory().getCategoryId()))
			{
				addFinancialCategory = false;
				break;
			}
		}
		if (addFinancialCategory) 
			financialCategories.add( new FinancialCategoryDTO(m.getAccount(),
					m.getSubaccount(), m.getCategory(), m.getBookAccount(),
							m.getFinancial().getAccountingDate(),
							m.getExchangeRate(), updateBalance, isOfficialValue, allowCurrencyAdjustment, dueDate));
	}

	private static void validateAccountingEquation(List<Movement> movements)
			throws Exception {
		BigDecimal debits = BigDecimal.ZERO;
		BigDecimal credits = BigDecimal.ZERO;

		for(Movement m : movements)
		{
			if (m.getDebitOrCredit().equals(DebitOrCredit.DEBIT))
				debits = debits.add(m.getOfficialValue().abs());
			else
				credits = credits.add(m.getOfficialValue().abs());
		}
		System.out.println("Validate Accounting Equation...");
		System.out.println(new StringBuffer("D:").append(debits).append("|C:").append(credits));
		if (credits.compareTo(debits)!=0)
			throw new InternalException("transaction_unbalanced", debits, credits);	
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
