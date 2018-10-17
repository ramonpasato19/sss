package com.powerfin.actions.accountInvoice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;
import org.openxava.util.DataSourceConnectionProvider;
import org.openxava.util.Is;
import org.w3c.dom.Document;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.AccountHelper;
import com.powerfin.helper.AccountInvoiceHelper;
import com.powerfin.helper.CompanyHelper;
import com.powerfin.helper.ElectronicVoucherHelper;
import com.powerfin.helper.ElectronicVoucherHelper.ElectronicVoucherResponce;
import com.powerfin.helper.ParameterHelper;
import com.powerfin.helper.ReportHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.Company;
import com.powerfin.model.File;
import com.powerfin.model.OperatingCondition;
import com.powerfin.model.dto.ReportDTO;
import com.powerfin.service.EmailSenderService;
import com.powerfin.service.EmailSenderService.Attachment;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


public class IssueElectronicInvoiceSaleSaveAction extends SaveAction{

	private String transactionModuleId;
	Integer branchId = null;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws Exception {

		Company company = XPersistence.getManager().find(Company.class, CompanyHelper.COMPANY_ID);
		EmailSenderService emailService;
		
		String accountId = getView().getValueString("accountId");

		Account account = XPersistence.getManager().find(Account.class, accountId);
		
		OperatingCondition eOperatingCondition = XPersistence.getManager().find(OperatingCondition.class, AccountInvoiceHelper.OPERATING_CONDITION_ISSUE_ELECTRONICALLY);
		File eDocumentOld = null;
		
		if (account.getElectronicDocument() != null)
		{
			eDocumentOld = XPersistence.getManager().find(File.class, account.getElectronicDocument());
		}
				
		validate(account);
		
		ElectronicVoucherHelper helper = new ElectronicVoucherHelper();
		Document xmlDocument = helper.getInvoiceSaleXMLDocument(account);
		
		File signatureFile = CompanyHelper.getElectronicSignature();
		String passSignature = (String)ParameterHelper.getValue("PASSWORD_ELECTRONIC_SIGNATURE");
		InputStream signature = new ByteArrayInputStream(signatureFile.getData());
		
		Document xmlDocumentSigned = helper.signXMLDocument(xmlDocument, signature, passSignature);
		System.out.println("document_signed");
		ElectronicVoucherResponce evr = helper.authorizeXMLDocument(account, xmlDocumentSigned);
		
		File eDocument = new File();
		eDocument.setName(account.getAccountId()+".xml");
		eDocument.setData(helper.XMLDocumentToByteArray(evr.getXmlDocument()));
		eDocument.setId("E-"+account.getAccountId());
		
		if (eDocumentOld != null)
		{
			XPersistence.getManager().remove(eDocumentOld);
		}
		
		XPersistence.getManager().persist(eDocument);	
		
		if (evr.getResult() == 0)
		{
			System.out.println("document_authorized");
			ReportDTO report = ReportHelper.findReportByName(ParameterHelper.getValue("ELECTRONIC_INVOICE_SALE_PDF"));
			
			JasperReport jReport = JasperCompileManager.compileReport(report.getJrxml());
			Map parameters = new HashMap();			
			parameters.put("ACCOUNT", account.getAccountId());
			parameters.put("SCHEMA", company.getOxorganizationId().toLowerCase());
			parameters.put("AUTHORIZATION_CODE", ElectronicVoucherHelper.getAccessKey(xmlDocumentSigned));
			
			JasperPrint jprint = null;

			Connection con = null;
			try {
				con = DataSourceConnectionProvider.getByComponent(getView().getModelName()).getConnection();
				con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); // To avoid freezing the application with some reports in some databases
				if (!Is.emptyString(XPersistence.getDefaultSchema())) {
					con.setCatalog(XPersistence.getDefaultSchema());
				}
				jprint = JasperFillManager.fillReport(jReport, parameters, con);
			} finally {
				con.close();
			}
			
			byte[] documentPDF = JasperExportManager.exportReportToPdf(jprint);
			
			List<String> toRecipients = new ArrayList<String>();
			toRecipients.add(account.getPerson().getEmail());
			List<String> ccRecipients = new ArrayList<String>();

			Attachment attachmentXML = new Attachment(account.getCode(), eDocument.getData(), "application/xml", "xml");
			Attachment attachmentPDF = new Attachment(account.getCode(), documentPDF, "application/pdf", "pdf");
			
			emailService = new EmailSenderService(
					toRecipients,
					ccRecipients,
					company.getPerson().getName()+" - COMPROBANTE ELECTRONICO",
					getBodyContent(company.getPerson().getName(), account.getName().toUpperCase(), account.getCode()),
					attachmentXML, attachmentPDF
					);
			emailService.init();
			
			account.setOperatingCondition(eOperatingCondition);
			AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
			accountInvoice.setAuthorizationCode(ElectronicVoucherHelper.getAccessKey(xmlDocumentSigned));
			XPersistence.getManager().merge(accountInvoice);
			
			account.setElectronicDocument(eDocument.getId());
			account = AccountHelper.updateAccount(account);
			
			addMessage("account_modified", account.getClass().getName());
			
			emailService.sendEmail();
		
		}
		else
		{
			account.setElectronicDocument(eDocument.getId());
			account = AccountHelper.updateAccount(account);
			
			System.out.println("document_NO_authorized");
			addError("Error al Emitir documento, revisar el documento electronico. Error: "+evr.getMessage());
		}
		
		getView().refresh();
	}
	
	private void validate(Account account) throws Exception {
		
		if (account.getOperatingCondition().getOperatingConditionId().equals(AccountInvoiceHelper.OPERATING_CONDITION_ISSUE_ELECTRONICALLY))
			throw new OperativeException("account_has_already_been_issued_electronically", account.getCode());
				
		
		Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
 
		if (account.getPerson().getEmail() == null) {
        	throw new OperativeException("person_does_not_have_an_email");
        }
		else
		{
	        Matcher mather = pattern.matcher(account.getPerson().getEmail());
	 
	        if (mather.find() != true) {
	        	throw new OperativeException("person_does_not_have_a_valid_email");
	        }
		}
	}

	public String getTransactionModuleId() {
		return transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
	
	private String getBodyContent(String companyName, String customerName, String invoiceCode)
	{
		return new StringBuilder().append("Estimado cliente ")
				.append(customerName)
				.append("<br>")
				.append("Reciba un cordial saludo de quienes hacemos ")
				.append(companyName)
				.append(". Nos complace informale que su documento electrónico ha sido generado con el siguiente detalle: ")
				.append("<br><br>")
				.append("Tipo de documento: FACTURA")
				.append("<br>")
				.append("Documento electrónico No: ")
				.append(invoiceCode)
				.toString();
	}
	
}
