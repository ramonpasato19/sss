package com.powerfin.helper.integrations;

import java.math.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class PrestashopHelper {
	
	private String prefix;
	int secuenciaACT = 0;
	int proccessInvoice = 0;
	int notProcessInvoice = 0;
	String errors;
	

	public PrestashopHelper(String prefix)
	{
		this.prefix = prefix;
	}
	public void addInventory(Connection connection, int productId, BigDecimal quantity) {
		String update = "update ps_stock_available set quantity=(quantity+?) where id_product=?";
		try {
			// con esta sentencia se insertan los datos en la base de datos
			PreparedStatement pst = connection.prepareStatement(update);
			pst.setBigDecimal(1, quantity);
			pst.setInt(2, productId);
			pst.executeUpdate();

		} catch (Exception e) {
			System.out.println("Error PrestashopHelper:" + e.getMessage());
			throw new XavaException("send_stock_error");
		}
	}

	public void removeInventory(Connection connection, int productId, BigDecimal quantity) {
		String update = "update "+prefix+"_stock_available set quantity=(quantity-?) where id_product=?";
		try {
			// con esta sentencia se insertan los datos en la base de datos
			PreparedStatement pst = connection.prepareStatement(update);
			pst.setBigDecimal(1, quantity);
			pst.setInt(2, productId);
			pst.executeUpdate();

		} catch (Exception e) {
			System.out.println("Error PrestashopHelper:" + e.getMessage());
			throw new XavaException("send_stock_error");
		}
	}

	/*
	 * Metodo para recuperar las facturas de Prestachop (Mysql)
	 * 
	 */
	public int pullInvoice(Connection connection, Date fromDate, Date toDate, String sequentialCode) throws Exception {
		
		int invoicesFinished = 0;
		int invoicesErrors = 0;
		StringBuffer errores = new StringBuffer();
		
		String QUERY_INVOICE = "select o.id_order, o.id_customer, a.dni, c.firstname, c.lastname, c.id_gender, a.phone_mobile, c.email, a.address1, a.address2, a.phone,"
				+ " o.invoice_date, '001' as status_invoice, total_paid_tax_incl from "+prefix+"_orders o, "+prefix+"_customer c, "+prefix+"_address a where o.id_customer=c.id_customer and "
				+ " o.id_address_invoice=a.id_address and c.active=true and a.active =true and current_state in (2,4,5,12,14) "
				+ " and (date(invoice_date) >= ? and date(invoice_date) <= ?)";
		
		if (fromDate == null)
			fromDate = CompanyHelper.getCurrentAccountingDate();
		if (toDate == null)
			toDate = CompanyHelper.getCurrentAccountingDate();

		PreparedStatement query = connection.prepareStatement(QUERY_INVOICE);
		query.setDate(1, new java.sql.Date(fromDate.getTime()));
		query.setDate(2, new java.sql.Date(toDate.getTime()));
		ResultSet result = query.executeQuery();

		while (result.next()) {
			try {
				if (putInvoice(connection, result,sequentialCode)) {
					invoicesFinished++;
				}
			} catch (Exception e) {
				if (errores.length() > 0)
					errores.append(",");
				errores.append(result.getString("id_order"));
				invoicesErrors++;
			}
		}
		proccessInvoice = invoicesFinished;
		notProcessInvoice = invoicesErrors;
		errors = errores.toString();
		return invoicesFinished;
	}

	@SuppressWarnings("unchecked")
	public boolean putInvoice(Connection connection, ResultSet result,String codeSequential) throws Exception {
		List<Account> invoiceFinds = (List<Account>) XPersistence.getManager()
				.createQuery("select a from Account a where account_id like'FVE%' and externalCode=:externalcode")
				.setParameter("externalcode", result.getString("id_order")).getResultList();
		if (!invoiceFinds.isEmpty()) {
			return false;
		}

		Person customer = null;
		List<Person> customers = null;
		String cedula=null;
		if(result.getString("dni") == null)
		{
			cedula="5555555555";
		}else{
			cedula=removeEspecialCharacters(result.getString("dni"));
		}
		customers = (List<Person>) XPersistence.getManager()
				.createQuery("select p from Person p where p.identification = :identification")
				.setParameter("identification", cedula).getResultList();
		if (!customers.isEmpty()) {
			customer = customers.get(0);
		}

		if (customer == null) {
			// Si es null el cliente es nuevo
			customer = new Person();
			// Se crea la persona
			customer.setIdentification(result.getString("dni"));
			customer.setName(result.getString("lastname") + " " + result.getString("firstname"));
			customer.setEmail(result.getString("email"));
			customer.setUserRegistering("admin");

			if (result.getString("dni").length() == 13) {
				// Persona Juridica
				IdentificationType identificationType = XPersistence.getManager().find(IdentificationType.class, "RUC");
				customer.setIdentificationType(identificationType);
				PersonType personType = XPersistence.getManager().find(PersonType.class, "LEG");
				customer.setPersonType(personType);
				XPersistence.getManager().persist(customer);
				LegalPerson legalPerson = new LegalPerson();
				legalPerson.setPersonId(customer.getPersonId());
				legalPerson.setBusinessName(result.getString("lastname") + " " + result.getString("firstname"));
				legalPerson.setUserRegistering("admin");
				legalPerson.setHomeMainStreet(result.getString("address1"));
				legalPerson.setHomeSideStreet(result.getString("address2"));
				legalPerson.setHomePhoneNumber1(result.getString("phone"));
				XPersistence.getManager().persist(legalPerson);
			} else {
				// Persona Natural
				IdentificationType identificationType = XPersistence.getManager().find(IdentificationType.class, "CED");
				customer.setIdentificationType(identificationType);
				PersonType personType = XPersistence.getManager().find(PersonType.class, "NAT");
				customer.setPersonType(personType);
				XPersistence.getManager().persist(customer);
				NaturalPerson naturalPerson = new NaturalPerson();
				naturalPerson.setPersonId(customer.getPersonId());
				naturalPerson.setIdentification(customer.getIdentification());
				naturalPerson.setIdentificationType(identificationType);
				MaritalStatus maritalStatus = XPersistence.getManager().find(MaritalStatus.class, "999");
				naturalPerson.setMaritalStatus(maritalStatus);
				if (result.getInt("id_gender") == 1) {
					Gender gender = XPersistence.getManager().find(Gender.class, "M");
					naturalPerson.setGender(gender);
				} else {
					Gender gender = XPersistence.getManager().find(Gender.class, "F");
					naturalPerson.setGender(gender);
				}
				String[] names = result.getString("firstname").split(" ");
				String[] lastnames = result.getString("lastname").split(" ");
				if (names.length > 0) {
					naturalPerson.setFirstName(names[0]);
					String secondName = "";
					for (int i = 1; i < names.length; i++) {
						secondName = secondName + " " + names[i];
					}
					naturalPerson.setSecondName(secondName);
				}
				if (lastnames.length > 0) {
					naturalPerson.setPaternalSurname(lastnames[0]);
					String secondSurname = "";
					for (int i = 1; i < lastnames.length; i++) {
						secondSurname = secondSurname + " " + lastnames[i];
					}
					naturalPerson.setMaternalSurname(secondSurname);
				}
				naturalPerson.setCellPhoneNumber1(result.getString("phone_mobile"));
				naturalPerson.setHomeMainStreet(result.getString("address1"));
				naturalPerson.setHomeSideStreet(result.getString("address2"));
				naturalPerson.setHomePhoneNumber1(result.getString("phone"));
				naturalPerson.setUserRegistering("admin");
				XPersistence.getManager().persist(naturalPerson);
			}
		}
		String secuencialCode = getSecuencialCode(codeSequential);
		String establishmentCode  = ParameterHelper.getValue("ESTABLISHMENT_CODE") ;
		String emissionPointCode = ParameterHelper.getValue("EMISSION_POINT_CODE");
		
		Product product = XPersistence.getManager().find(Product.class, "102");
		Account account = AccountHelper.createAccount(product.getProductId(), customer.getPersonId(), AccountHelper.getDefaultAccountStatusByProduct(product).getAccountStatusId(), null,
				establishmentCode + "-"+ emissionPointCode + "-"+ secuencialCode, null);
		account.setExternalCode(result.getString("id_order"));
		AccountInvoice accountInvoice = new AccountInvoice();
		accountInvoice.setAccountId(account.getAccountId());
		accountInvoice.setAccount(account);
		accountInvoice.setPerson(customer);
		accountInvoice.setProduct(account.getProduct());
		accountInvoice.setIssueDate(result.getDate("invoice_date"));
		accountInvoice.setRegistrationDate(result.getDate("invoice_date"));
		InvoiceVoucherType typeInvoice = XPersistence.getManager().find(InvoiceVoucherType.class, "01");
		accountInvoice.setInvoiceVoucherType(typeInvoice);
		accountInvoice.setRemark("FACTURA GENERADA DESDE YALOBOX");
		accountInvoice.setUserRegistering("admin");
		accountInvoice.setEstablishmentCode(establishmentCode);
		accountInvoice.setEmissionPointCode(emissionPointCode);
		accountInvoice.setSequentialCode(secuencialCode);
		accountInvoice.setAuthorizationCode(ParameterHelper.getValue("AUTHORIZATION_CODE"));
		Unity unity = XPersistence.getManager().find(Unity.class, ParameterHelper.getValue("DEFAULT_UNITY_ID"));
		accountInvoice.setUnity(unity);
		XPersistence.getManager().persist(accountInvoice);
		PreparedStatement query = connection.prepareStatement(
				"SELECT id_order, product_id, product_quantity, product_price, (od.reduction_amount_tax_excl+round(((product_price*reduction_percent)/100),4)) as reduction_amount, coalesce(t.rate,0) as rate , od.total_price_tax_excl, (od.total_price_tax_incl-od.total_price_tax_excl) as tax_amount, od.total_price_tax_incl "
						+ " FROM "+prefix+"_order_detail od LEFT JOIN "+prefix+"_tax_rule tr ON od.id_tax_rules_group=tr.id_tax_rule  LEFT JOIN "+prefix+"_tax t ON tr.id_tax=t.id_tax "
						+ " where  id_order=?");
		query.setInt(1, result.getInt("id_order"));
		ResultSet resultDetail = query.executeQuery();
		while (resultDetail.next()) {
			if (putInvoiceDetail(resultDetail, accountInvoice) == false) {
				throw new XavaException("account_item_not_found", resultDetail.getString("product_id"));
			}
		}
		TransactionModule transactionModule = XPersistence.getManager().find(TransactionModule.class, "INVOICE_SALE");
		Transaction transaction = new Transaction();
		transaction.setValue(result.getBigDecimal("total_paid_tax_incl"));
		transaction.setRemark("FACTURA GENERADA DESDE YALOBOX");
		transaction.setUserRequesting(Users.getCurrentUserInfo().getId());
		transaction.setRequestDate(result.getDate("invoice_date"));
		transaction.setTransactionModule(transactionModule);
		transaction.setExchangeRate(BigDecimal.ONE);
		transaction.setTransactionStatus(transactionModule.getDefaultTransactionStatus());
		transaction.setDebitAccount(accountInvoice.getAccount());
		transaction.setVersion(0);
		transaction.setAccountingDate(result.getDate("invoice_date"));
		transaction.setCurrency(accountInvoice.getAccount().getCurrency());
		XPersistence.getManager().persist(transaction);
		return true;
	}

	public boolean putInvoiceDetail(ResultSet result, AccountInvoice accountInvoice) throws Exception {
		AccountInvoiceDetail accountInvoiceDetail = new AccountInvoiceDetail();
		accountInvoiceDetail.setAccountInvoice(accountInvoice);
		try {
			Account product = (Account) XPersistence.getManager()
					.createQuery("from Account p where alternateCode=:alternatecode")
					.setParameter("alternatecode", result.getString("product_id")).getSingleResult();
			accountInvoiceDetail.setAccountDetail(product);
		} catch (Exception e) {
			return false;
		}
		Tax tax = null;
		if (result.getBigDecimal("tax_amount").compareTo(BigDecimal.ZERO) > 0)
			tax = XPersistence.getManager().find(Tax.class, "IVA14");
		else
			tax = XPersistence.getManager().find(Tax.class, "IVA0");
		accountInvoiceDetail.setTax(tax);
		accountInvoiceDetail.setQuantity(result.getBigDecimal("product_quantity"));
		accountInvoiceDetail.setUnitPrice(result.getBigDecimal("product_price"));
		if (result.getBigDecimal("reduction_amount").compareTo(BigDecimal.ZERO) > 0) {
			accountInvoiceDetail.setDiscount(result.getBigDecimal("reduction_amount"));
		} else {
			accountInvoiceDetail.setDiscount(BigDecimal.ZERO);
		}

		accountInvoiceDetail.setAmount(result.getBigDecimal("total_price_tax_excl"));
		accountInvoiceDetail.setTaxAmount(result.getBigDecimal("tax_amount"));
		accountInvoiceDetail.setFinalAmount(result.getBigDecimal("total_price_tax_incl"));
		XPersistence.getManager().persist(accountInvoiceDetail);
		return true;
	}

	public String getSecuencialCode(String codeSequential) throws NumberFormatException, Exception {
		if (secuenciaACT > 0) {
			secuenciaACT++;
			String secuencialString = secuenciaACT + "";
			for (int i = secuencialString.length(); i < 7; i++) {
				secuencialString = "0" + secuencialString;
			}
			return secuencialString;
		} else {
			int sequetial = Integer.parseInt(codeSequential);
			secuenciaACT = sequetial;
			String secuencialString = sequetial + "";
			for (int i = secuencialString.length(); i < 7; i++) {
				secuencialString = "0" + secuencialString;
			}
			return secuencialString;
		}
	}

	public int getProccessInvoice() {
		return proccessInvoice;
	}

	public void setProccessInvoice(int proccessInvoice) {
		this.proccessInvoice = proccessInvoice;
	}

	public int getNotProcessInvoice() {
		return notProcessInvoice;
	}

	public void setNotProcessInvoice(int notProcessInvoice) {
		this.notProcessInvoice = notProcessInvoice;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}
	
	public String removeEspecialCharacters(String text){
		String auxiliarText=text;
		auxiliarText=auxiliarText.replace("%","");
		auxiliarText=auxiliarText.replace("-","");
		auxiliarText=auxiliarText.replace("/","");
		auxiliarText=auxiliarText.replace("º","");
		auxiliarText=auxiliarText.replace("!","");
		auxiliarText=auxiliarText.replace("_","");
		auxiliarText=auxiliarText.replace("@","");
		auxiliarText=auxiliarText.replace("#","");
		auxiliarText=auxiliarText.replace("$","");
		auxiliarText=auxiliarText.replace("(","");
		auxiliarText=auxiliarText.replace(")","");
		auxiliarText=auxiliarText.replace("=","");
		auxiliarText=auxiliarText.replace("?","");
		auxiliarText=auxiliarText.replace("¿","");
		auxiliarText=auxiliarText.replace(".","");
		return auxiliarText;
	}
}
