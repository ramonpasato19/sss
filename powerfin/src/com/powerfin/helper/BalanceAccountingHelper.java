package com.powerfin.helper;

import java.sql.*;
import java.util.Date;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

public class BalanceAccountingHelper {

    private static Log log = LogFactory.getLog(BalanceAccountingHelper.class);

	public static void generateBalanceSheetOld(Date accountingDate) {
		Connection con = null;
		try {
			if (!accountingDate.before(CompanyHelper.getCurrentAccountingDate()))
			{
				con = DataSourceConnectionProvider.getByComponent("Balance")
						.getConnection();
				CallableStatement cs = con.prepareCall("{ call generate_balance_sheet(?)}");
				cs.setDate(1, new java.sql.Date(accountingDate.getTime()));
				cs.executeUpdate();
				cs.close();
			}
		} catch (Exception ex) {
			throw new SystemException(ex);
		} finally {
			try {
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void generateBalanceSheet() {
		generateBalanceSheet(CompanyHelper.getCurrentAccountingDate());
	}
	public static void generateBalanceSheet(Date accountingDate) {
		
		if (!accountingDate.before(CompanyHelper.getCurrentAccountingDate()))
		{
			String schema = CompanyHelper.getSchema().toLowerCase();
			XPersistence.getManager()
			.createNativeQuery("delete from "+schema+".balance_accounting where accounting_date = :accountingDate")
			.setParameter("accountingDate", accountingDate)
			.executeUpdate();
			log.info("****Deleting BalanceAccounting: "+ accountingDate);
			String query = "insert into "+schema+".balance_accounting "
					+ "select :accountingDate accounting_date, "
					+ "b.book_account_id, "
					+ "(case when book.allow_currency_adjustment = 1 then "
						+ "COALESCE(round(sum(b.balance*ex.exchange_rate),2),0) "
					+ "else "
						+ "COALESCE(sum(b.official_balance),0) "
					+ "end) as official_balance, "
					+ "c.official_currency_id, "
			        + "COALESCE(sum(b.balance),0) as balance, "
			        + "b.currency_id "
			        + "from "+schema+".balance b, "+schema+".company c, "+schema+".exchange_rate_daily ex, "+schema+".book_account book "
			        + "where :accountingDate between b.from_date and b.to_date "
			        + "and c.company_id=1 "
			        + "and b.book_account_id = book.book_account_id "
			        + "and ex.currency_id=b.currency_id "
			        + "and :accountingDate between ex.from_date and ex.to_date "
			        + "group by b.book_account_id,b.currency_id,c.official_currency_id,book.allow_currency_adjustment";
			log.info("****Script: "+ query);
			XPersistence.getManager()
			.createNativeQuery(query)
			.setParameter("accountingDate", accountingDate)
			.executeUpdate();
			
			log.info("****Inserting movements on BalanceAccounting: "+ accountingDate);
			
			for (int level=10; level>1; level--)
			{
				XPersistence.getManager()
				.createNativeQuery("insert into "+schema+".balance_accounting "
			        + "select bacc.accounting_date, "
			        + "ba.book_account_parent as book_account_id, "
			        + "COALESCE(sum(bacc.official_balance),0) as official_balance, "
			        + "bacc.official_currency_id, "
			        + "null balance, "
			        + "null currency_id "
			        + "from "+schema+".balance_accounting bacc, "+schema+".book_account ba "
			        + "where bacc.accounting_date = :accountingDate "
			        + "and bacc.book_account_id = ba.book_account_id "
			        + "and ba.level = :level "
			        + "group by bacc.accounting_date, "
			        + "ba.book_account_parent, bacc.official_currency_id;")
				.setParameter("accountingDate", accountingDate)
				.setParameter("level", level)
				.executeUpdate();
				log.info("****Roolup level:"+level+" on BalanceAccounting: "+ accountingDate);
			}
		}	
	}
}
