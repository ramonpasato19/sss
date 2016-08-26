package com.powerfin.actions.transaction;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXAuthorizeInBatchSaveAction extends TabBaseAction {

	@Override
	public void execute() throws Exception {
		authorizeTransaction();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void authorizeTransaction() throws Exception {
		Map values = new HashMap(); // Valores a asignar a cada entidad para
									// marcarla
		TransactionStatus transactionStatus =XPersistence.getManager().find(TransactionStatus.class, "002"); 
		values.put("transactionStatus", transactionStatus); // Pone deleted a true
		for (Map key : getSelectedKeys()) { // Itera sobre todas las filas
											// seleccionadas
			//Obtenemos la clave de cada entidad
			Transaction transaction = (Transaction) MapFacade.findEntity(getView().getModelName(), key);
			try {
				//MapFacade.setValues(getTab().getModelName(), key, values);// Modificamos cada entidad
				transaction.setTransactionStatus(transactionStatus);
				TransactionHelper.processTransaction(transaction);
				
			} catch (ValidationException ex) { // Si se produce una
												// ValidationException..
				addError("no_authorize_transaction", transaction.getVoucher());
				addErrors(ex.getErrors()); // ...mostramos los mensajes
				ex.printStackTrace();
				throw ex;
			} catch (Exception ex) { // Si se lanza cualquier otra excepción, se añade
				addError("no_authorize_transaction", transaction.getVoucher());
				ex.printStackTrace();
				throw ex;
			}
		}
		addMessage("transactions_authorized");
		getTab().deselectAll(); // Después de borrar deseleccionamos la filas
		resetDescriptionsCache(); // Y reiniciamos el caché de los combos para
									// este usuario
	}
}
