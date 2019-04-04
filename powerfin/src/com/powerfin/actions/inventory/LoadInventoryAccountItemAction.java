package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.helper.*;
import com.powerfin.model.Branch;
import com.powerfin.model.KardexAccountTemp;
import com.powerfin.util.report.*;

public class LoadInventoryAccountItemAction extends ViewBaseAction {
	
	
	@Override
	public void execute() throws Exception {
		Date fromDate = (Date)getView().getValue("fromDate");
		Date toDate = (Date)getView().getValue("toDate");				
		if (fromDate==null) {
			fromDate = CompanyHelper.getCurrentAccountingDate();			
		}
		if (toDate==null) {
			toDate = CompanyHelper.getCurrentAccountingDate();			
		}
		
		List<Branch> branchs = XPersistence.getManager().createQuery("select distinct b from Branch b").getResultList();
		if (branchs!=null && !branchs.isEmpty()) {
			XPersistence.getManager().createQuery("DELETE FROM KardexAccountTemp k").executeUpdate();
			XPersistence.commit();
			System.out.println("ELEMENTOS BORRADOS");
			Query queryItems = XPersistence.getManager().createNativeQuery(getNativeQueryItems(XPersistence.getDefaultSchema()));
			List<String> accountItems = queryItems.getResultList();
			int index = 1; 
			for (String currentAccoutItem:accountItems) {
				for (Branch b:branchs) {
					insertValuesInKardex(fromDate, toDate, currentAccoutItem, b.getName(), b.getBranchId());					
				}				
				System.out.println("INSERTANDO MOVIMIENTOS PROGRESO "+index +" de "+accountItems.size());								
				index++;
			}		
			XPersistence.commit();			
		}				
	}
	
	/**
	 * Método que se ocupa de cargar lo valores en la tabla kardex, 
	 *  para lo cual primero se obtinen los datyos y luego se actualizan los
	 *  costos de manera que se pueda obtener el costo promedio ponderado 
	 * @param fromDate
	 * @param toDate
	 * @param accountItemId
	 * @param branchName
	 * @return
	 */
	private boolean insertValuesInKardex(Date fromDate, Date toDate, String accountItemId , String branchName, Integer brachId) {
		
		String schema = XPersistence.getDefaultSchema();		
		Query query = XPersistence.getManager().createNativeQuery(getNaviteQueryKardex(schema));
		query.setParameter("PREMARK", "%MIGRACION%");
		query.setParameter("PBRANCH_NAME", branchName);
		query.setParameter("PFROM_DATE", fromDate);
		query.setParameter("PTO_DATE", toDate);
		query.setParameter("PACCOUNT_ITEM_ID",accountItemId);
		
		List<Object[]> result = query.getResultList();
		
		int index = 0;
		List<KardexAccountTemp> kardexList = new ArrayList<KardexAccountTemp>();
		for (Object []data:result) {
			KardexAccountTemp k = new KardexAccountTemp();
			k.setAccountId((String)data[0]+ "_"+index+"_"+brachId);
			k.setIssueDate((Date)data[1]);
			k.setRemark((String)data[2]);
			k.setUnitCost((BigDecimal)data[3]);
			k.setAccountItemId((String)data[4]);			
			k.setIncomes((BigDecimal)data[5]);
			k.setExpenses((BigDecimal)data[6]);
			k.setBalance((BigDecimal)data[7]);
			k.setBranchId((Integer)data[8]);
			k.setTotalCost((BigDecimal)data[9]);
			k.setAccumulateBalance((BigDecimal)data[10]);
			k.setAccumulateTotalCost((BigDecimal)data[11]);
			k.setAverageCost((BigDecimal)data[12]);			
			if (index>0) {
				if (k.getUnitCost().compareTo(BigDecimal.ZERO)==0) {
					k.setUnitCost( kardexList.get(index-1).getUnitCost());
					k.setTotalCost(k.getUnitCost().multiply(k.getBalance()));
					k.setAccumulateTotalCost(kardexList.get(index-1).getAccumulateTotalCost().add(k.getTotalCost()));
					if (k.getAccumulateTotalCost().compareTo(BigDecimal.ZERO)==0) {
						k.setAverageCost(BigDecimal.ZERO);
					}else {
						k.setAverageCost(k.getAccumulateTotalCost().divide(k.getAccumulateTotalCost(),5, RoundingMode.HALF_UP));						
					}
				}
			}
			k.setRegistrationDate(new Date());
			kardexList.add(k);
			index++;
			XPersistence.getManager().persist(k);
			XPersistence.commit();
			
		}
		
		return false;
	}
	
	/**
	 * Creación de consulta que recupera todos lo items activos
	 * @param schema
	 * @return
	 */
	private String getNativeQueryItems(String schema) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT  ");
		sql.append("	DISTINCT AI.ACCOUNT_ID  ");
		sql.append("FROM  ");
		sql.append("	"+schema+".ACCOUNT_ITEM AI, ");
		sql.append("	"+schema+".ACCOUNT A  ");
		sql.append("WHERE  ");
		sql.append("	A.ACCOUNT_ID = AI.ACCOUNT_ID ");
		sql.append("	AND A.ACCOUNT_STATUS_ID  = '002' ");
		sql.append("	AND A.PRODUCT_ID IN ('1050', '1051','1052','1053') ");
		return sql.toString();
	}
	
	
	
	/**
	 * Consulta que permite recuperar los datos para 
	 * ingresar en la tabal temporal de KARDEX
	 * @return
	 */
	private String getNaviteQueryKardex(String schema) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");		 
		sql.append("	X.ACCOUNT_ID , ");
		sql.append("	X.ISSUE_DATE, ");
		sql.append("	X.REMARK, ");
		sql.append("	X.UNIT_COST, ");
		sql.append("	X.ACCOUNT_DETAIL_ID, ");
		sql.append("	X.INCOME, ");
		sql.append("	X.EXPENSES, ");
		sql.append("	X.BALANCE, ");
		sql.append("	X.BRANCH_ID, ");
		sql.append("    (ABS(X.UNIT_COST) * ABS(X.BALANCE)) as TOTAL_COSTO, ");  
		sql.append("    SUM (SUM(X.BALANCE)) OVER (ORDER BY X.ISSUE_DATE ASC) AS BALANCE_FINAL, "); 
		sql.append("    SUM (SUM(ABS(X.UNIT_COST) * ABS(X.BALANCE))) OVER (ORDER BY X.ISSUE_DATE ASC) as ACUMMULATE_COST, ");
		sql.append("    ( ");
		sql.append("    	CASE WHEN SUM (SUM(ABS(X.BALANCE))) OVER (ORDER BY X.ISSUE_DATE ASC)>0 THEN ");
		sql.append("    		SUM (SUM(ABS(X.UNIT_COST) * ABS(X.BALANCE))) OVER (ORDER BY X.ISSUE_DATE ASC) / SUM (SUM(ABS(X.BALANCE))) OVER (ORDER BY X.ISSUE_DATE ASC) "); 
		sql.append("    	ELSE ");
		sql.append("    		0 ");
		sql.append("    	END  ");
		sql.append("	) AS AVERAGE_COST ");	
		sql.append("FROM  ");
		sql.append("	( ");
		sql.append("        SELECT "); 
		sql.append("	        'INVENTARIO_' || :PACCOUNT_ITEM_ID  AS ACCOUNT_ID, ");
		sql.append("            TO_DATE('01-01-2000', 'DD-MM-YYYY') AS ISSUE_DATE, "); 
		sql.append("            'CARGA INICIAL' AS REMARK, ");
		sql.append("            COALESCE( ");  
		sql.append("                ( ");
		sql.append("                    SELECT (TA.VALUE / TA.QUANTITY) ");
		sql.append("					FROM "+schema+".TRANSACTION_ACCOUNT TA, ");
		sql.append("					"+schema+".TRANSACTION T ");
		sql.append("					WHERE ");
		sql.append("					TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("					AND T.REMARK LIKE :PREMARK ");
		sql.append("					AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID ");  
		sql.append("					AND TA.CATEGORY_ID = 'COST'  ");
		sql.append("					AND TA.DEBIT_OR_CREDIT = 'D' ");
		sql.append("                    AND BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) ");                     		                    
		sql.append("                    LIMIT 1 ");
		sql.append("                ) , ");
		sql.append("                0 ");
		sql.append("            )	AS UNIT_COST, "); 
		sql.append("            :PACCOUNT_ITEM_ID AS ACCOUNT_DETAIL_ID, "); 
		sql.append("            0 AS INCOME,  ");
		sql.append("            0 AS EXPENSES,  ");
		sql.append("            COALESCE(  ");             
		sql.append("                ( ");
		sql.append("                    SELECT TA.QUANTITY ");
		sql.append("					FROM "+schema+".TRANSACTION_ACCOUNT TA, ");
		sql.append("					"+schema+".TRANSACTION T ");
		sql.append("					WHERE ");
		sql.append("					TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("					AND T.REMARK LIKE :PREMARK ");
		sql.append("					AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID   ");
		sql.append("					AND TA.CATEGORY_ID = 'COST'  ");
		sql.append("					AND TA.DEBIT_OR_CREDIT = 'D' ");
		sql.append("                    AND BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) ");
		sql.append("                    LIMIT 1 ");
		sql.append("                ), ");
		sql.append("                0 ");
		sql.append("            ) AS BALANCE, ");				
		sql.append("	        (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) AS BRANCH_ID  ");
		sql.append("        UNION  ");
		sql.append("		SELECT  ");
		sql.append("            AI.ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID, ");
		sql.append("            AI.ISSUE_DATE,  ");
		sql.append("            AI.REMARK,  ");
		sql.append("            0 AS UNIT_COST, ");
		sql.append("            AID.ACCOUNT_DETAIL_ID, "); 
		sql.append("            0 AS INCOME,  ");
		sql.append("            AID.QUANTITY AS EXPENSES, (0-AID.QUANTITY) AS BALANCE, ");
		sql.append("	        A.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM ");
		sql.append("        	"+schema+".ACCOUNT_INVOICE AI, ");
		sql.append("        	"+schema+".ACCOUNT_INVOICE_DETAIL AID, ");
		sql.append("        	"+schema+".ACCOUNT A ");
		sql.append("		WHERE ");
		sql.append("        	AI.ACCOUNT_ID = AID.ACCOUNT_INVOICE_ID ");
		sql.append("        	AND AI.ACCOUNT_ID = A.ACCOUNT_ID ");
		sql.append("        	AND A.PRODUCT_ID = '102' ");
		sql.append("        	AND AID.ACCOUNT_DETAIL_ID = :PACCOUNT_ITEM_ID "); 
		sql.append("        	AND TO_DATE(TO_CHAR(AI.ISSUE_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD') BETWEEN  :PFROM_DATE AND :PTO_DATE ");        	
		sql.append("        	AND A.BRANCH_ID  IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) ");
		sql.append("		UNION ");
		sql.append("		SELECT  ");
		sql.append("	        AI.ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID, ");
		sql.append("	        AI.ISSUE_DATE, ");
		sql.append("	        AI.REMARK,  ");
		sql.append("	        AID.UNIT_PRICE as UNIT_COST, ");                
		sql.append("	        AID.ACCOUNT_DETAIL_ID,  ");
		sql.append("	        AID.QUANTITY AS INCOME,  ");
		sql.append("	        0 AS EXPENSES,  ");
		sql.append("	        (AID.QUANTITY) AS BALANCE, ");
		sql.append("	        A.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM  ");
		sql.append("	        "+schema+".ACCOUNT_INVOICE AI, ");
		sql.append("	        "+schema+".ACCOUNT_INVOICE_DETAIL AID, ");
		sql.append("	        "+schema+".ACCOUNT A ");
		sql.append("		WHERE ");
		sql.append("	        AI.ACCOUNT_ID = AID.ACCOUNT_INVOICE_ID ");
		sql.append("	        AND AI.ACCOUNT_ID = A.ACCOUNT_ID ");
		sql.append("	        AND A.PRODUCT_ID = '202' ");
		sql.append("	        AND AID.ACCOUNT_DETAIL_ID = :PACCOUNT_ITEM_ID "); 
		sql.append("	        AND TO_DATE(TO_CHAR(AI.ISSUE_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD') BETWEEN :PFROM_DATE AND :PTO_DATE ");
		sql.append("			AND A.BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) ");
		sql.append("        UNION  ");
		sql.append("        SELECT  ");
		sql.append("        	TA.TRANSACTION_ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID AS ACCOUNT_ID, "); 
		sql.append("        	TA.REGISTRATION_DATE AS ISSUE_DATE,	 ");
		sql.append("        	T.REMARK,  ");
		sql.append("        	0 AS UNIT_COST, ");
		sql.append("        	TA.ACCOUNT_ID AS ACCOUNT_DETAIL_ID, "); 
		sql.append("        	TA.QUANTITY AS INCOME,  ");
		sql.append("        	0 AS EXPENSES ,  ");
		sql.append("        	TA.QUANTITY AS BALANCE, "); 
		sql.append("	        TA.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM  ");
		sql.append("        	"+schema+".ACCOUNT A, ");
		sql.append("        	"+schema+".TRANSACTION T, ");
		sql.append("        	"+schema+".TRANSACTION_ACCOUNT TA ");
		sql.append("		WHERE A.PRODUCT_ID IN ( '1061') ");
		sql.append("	        AND T.TRANSACTION_ID =A.ACCOUNT_ID ");
		sql.append("	        AND TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("	        AND TA.CATEGORY_ID = 'COST' ");
		sql.append("	        AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID "); 
		sql.append("	        AND TA.DEBIT_OR_CREDIT ='D' ");
		sql.append("	        AND TO_DATE(TO_CHAR(TA.REGISTRATION_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')  BETWEEN :PFROM_DATE AND :PTO_DATE ");
		sql.append("	        AND TA.BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME)  ");
		sql.append("        UNION  ");
		sql.append("        SELECT  ");
		sql.append("        	TA.TRANSACTION_ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID AS ACCOUNT_ID, "); 
		sql.append("        	TA.REGISTRATION_DATE AS ISSUE_DATE,  ");
		sql.append("        	T.REMARK, ");
		sql.append("        	0 AS UNIT_COST,  ");       
		sql.append("	        TA.ACCOUNT_ID AS ACCOUNT_DETAIL_ID,  ");
		sql.append("	        0 AS INCOME,  ");
		sql.append("	        TA.QUANTITY AS EXPENSES ,  ");
		sql.append("	        0-TA.QUANTITY AS BALANCE,  ");
		sql.append("	        TA.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM  ");
		sql.append("	        "+schema+".ACCOUNT A, ");
		sql.append("	        "+schema+".TRANSACTION T, ");
		sql.append("	        "+schema+".TRANSACTION_ACCOUNT TA ");
		sql.append("		WHERE A.PRODUCT_ID IN ( '1061') ");
		sql.append("	        AND T.TRANSACTION_ID =A.ACCOUNT_ID ");
		sql.append("	        AND TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("	        AND TA.CATEGORY_ID = 'COST' ");
		sql.append("	        AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID  ");
		sql.append("	        AND TA.DEBIT_OR_CREDIT ='C' ");
		sql.append("	        AND TO_DATE(TO_CHAR(TA.REGISTRATION_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')  BETWEEN :PFROM_DATE AND :PTO_DATE ");
		sql.append("	        AND TA.BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME)  ");
		sql.append("        UNION  ");
		sql.append("		SELECT  ");
		sql.append("	        TA.TRANSACTION_ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID AS ACCOUNT_ID,  ");
		sql.append("	        TA.REGISTRATION_DATE AS ISSUE_DATE, ");
		sql.append("	        T.REMARK,  ");
		sql.append("	        0 AS UNIT_COST, ");
		sql.append("        	TA.ACCOUNT_ID AS ACCOUNT_DETAIL_ID,  ");
		sql.append("        	TA.QUANTITY AS INCOME,  ");
		sql.append("        	0 AS EXPENSES ,  ");
		sql.append("        	TA.QUANTITY AS BALANCE, ");
		sql.append("	        TA.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM  ");
		sql.append("		        "+schema+".ACCOUNT A, ");
		sql.append("		        "+schema+".TRANSACTION T, ");
		sql.append("		        "+schema+".TRANSACTION_ACCOUNT TA ");
		sql.append("		WHERE A.PRODUCT_ID IN ( '1060') ");
		sql.append("	        AND T.TRANSACTION_ID =A.ACCOUNT_ID ");
		sql.append("	        AND TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("	        AND TA.CATEGORY_ID = 'COST' ");
		sql.append("	        AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID  ");
		sql.append("	        AND TA.DEBIT_OR_CREDIT ='D' ");
		sql.append("	        AND TO_DATE(TO_CHAR(TA.REGISTRATION_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')  BETWEEN :PFROM_DATE AND :PTO_DATE ");
		sql.append("	        AND TA.BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME)  ");
		sql.append("        UNION  ");
		sql.append("		SELECT  ");
		sql.append("	        TA.TRANSACTION_ACCOUNT_ID || '_' || :PACCOUNT_ITEM_ID AS ACCOUNT_ID,  ");
		sql.append("	        TA.REGISTRATION_DATE AS ISSUE_DATE,  ");
		sql.append("	        T.REMARK, ");
		sql.append("	        0 AS UNIT_COST, ");
		sql.append("	        TA.ACCOUNT_ID AS ACCOUNT_DETAIL_ID,  ");
		sql.append("	        0 AS INCOME,  ");
		sql.append("	        TA.QUANTITY AS EXPENSES ,  ");
		sql.append("	        0-TA.QUANTITY AS BALANCE,  ");
		sql.append("	        TA.BRANCH_ID AS BRANCH_ID  ");
		sql.append("		FROM  ");
		sql.append("	        "+schema+".ACCOUNT A, ");
		sql.append("	        "+schema+".TRANSACTION T, ");
		sql.append("	        "+schema+".TRANSACTION_ACCOUNT TA ");
		sql.append("		WHERE A.PRODUCT_ID IN ( '1060') ");
		sql.append("	        AND T.TRANSACTION_ID =A.ACCOUNT_ID ");
		sql.append("	        AND TA.TRANSACTION_ID = T.TRANSACTION_ID ");
		sql.append("	        AND TA.CATEGORY_ID = 'COST' ");
		sql.append("	        AND TA.ACCOUNT_ID = :PACCOUNT_ITEM_ID  ");
		sql.append("	        AND TA.DEBIT_OR_CREDIT ='C' ");
		sql.append("	        AND TO_DATE(TO_CHAR(TA.REGISTRATION_DATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')  BETWEEN :PFROM_DATE AND :PTO_DATE ");
		sql.append("	        AND TA.BRANCH_ID IN (SELECT B.BRANCH_ID  FROM "+schema+".BRANCH B WHERE B.NAME LIKE :PBRANCH_NAME) ");
		sql.append("	)X  ");
		sql.append("	GROUP BY 1,2,3,4,5,6,7,8,9 ");
		sql.append("ORDER BY X.ISSUE_DATE, X.ACCOUNT_ID ");
		
		return sql.toString();
				
	}


	
}
