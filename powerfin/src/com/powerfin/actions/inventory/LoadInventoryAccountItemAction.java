package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Users;

import com.powerfin.helper.*;
import com.powerfin.model.Branch;
import com.powerfin.model.KardexAccountTemp;
import com.powerfin.model.Parameter;
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
		
		List<Branch> branchs = XPersistence.getManager().createQuery("select distinct b from Branch b  order by branchId").getResultList();
		if (branchs!=null && !branchs.isEmpty()) {
			XPersistence.getManager().createQuery("DELETE FROM KardexAccountTemp k where k.userRegistering = :PCURRENT_USER ")
				.setParameter("PCURRENT_USER", Users.getCurrent())
				.executeUpdate();
			XPersistence.commit();
			System.out.println("MOVIMIENTOS BORRADOS");
			Query queryItems = XPersistence.getManager().createNativeQuery(getNativeQueryItems(XPersistence.getDefaultSchema()));
			List<Object[]> accountItems =queryItems.getResultList();
			if (accountItems!=null && !accountItems.isEmpty()) {
				
				int index = 1; 
				for (Object []currentAccoutItem:accountItems) {
					
					for (Branch b:branchs) {
						insertValuesInKardex(fromDate, toDate, (String)currentAccoutItem[0], (BigDecimal)currentAccoutItem[1], b.getName(), b.getBranchId());
					}
					System.out.println("INSERTANDO MOVIMIENTOS EN "+currentAccoutItem[0]+" PROGRESO "+index +" de "+accountItems.size()+ " ["+Users.getCurrent()+"]");
					index++;
				}
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
	private boolean insertValuesInKardex(Date fromDate, Date toDate, String accountItemId , BigDecimal costLastPurchase, String branchName, Integer brachId) {
		
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
			k.setAccountId((String)data[0]+ "_"+index+"_"+brachId+"_"+Users.getCurrent());
			k.setIssueDate((Timestamp)data[1]);
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
			k.setUserMovement((String)data[13]);		
			k.setUserRegistering(Users.getCurrent());
			k.setRegistrationDate(new Date());
			if (index>0) {
				if (k.getUnitCost()==null || k.getUnitCost().compareTo(BigDecimal.ZERO)==0) {
					if ( kardexList.get(index-1).getAverageCost()==null &&  kardexList.get(index-1).getAverageCost().compareTo(BigDecimal.ZERO)==0) {
						k.setUnitCost(costLastPurchase);
						k.setAverageCost(costLastPurchase);
					}else {
						k.setUnitCost( kardexList.get(index-1).getAverageCost());
						k.setAverageCost(kardexList.get(index-1).getAverageCost());
					}
					k.setTotalCost(k.getUnitCost().multiply(k.getBalance()));
					k.setAccumulateTotalCost(kardexList.get(index-1).getAccumulateTotalCost().add(k.getTotalCost()));
				}
				if (index>0) {
					k.setAccumulateBalance(kardexList.get(index-1).getAccumulateBalance().add(k.getBalance()));					
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
		sql.append("	DISTINCT AI.ACCOUNT_ID, COALESCE(AI.COST_LAST_PURCHASE, 0)  ");
		sql.append("FROM  ");
		sql.append("	"+schema+".ACCOUNT_ITEM AI, ");
		sql.append("	"+schema+".ACCOUNT A  ");
		sql.append("WHERE  ");
		sql.append("	A.ACCOUNT_ID = AI.ACCOUNT_ID ");
		sql.append("	AND A.PRODUCT_ID IN ('1050', '1051','1052','1053') ");
		return sql.toString();
	}
	
	
	
	/**
	 * Consulta que permite recuperar los datos para 
	 * ingresar en la tabla temporal de KARDEX
	 * @return
	 */
	private String getNaviteQueryKardex(String schema) {
		String sql = null;
		try {
			sql = ParameterHelper.getValue("SCRIPT_KARDEX");
			sql = sql.replace("schema", schema);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return sql;				
	}
	
}
