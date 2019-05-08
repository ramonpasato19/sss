package com.powerfin.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.openxava.jpa.XPersistence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.powerfin.exception.InternalException;
import com.powerfin.exception.OperativeException;
import com.powerfin.model.Account;
import com.powerfin.model.AccountInvoice;
import com.powerfin.model.AccountInvoiceDetail;
import com.powerfin.model.AccountRetention;
import com.powerfin.model.AccountRetentionDetail;
import com.powerfin.model.Person;
import com.powerfin.model.Tax;
import com.powerfin.model.TaxType;
import com.powerfin.util.UtilApp;
import com.powerfin.util.externalWS.SendVoucherWS;
import com.thoughtworks.xstream.XStream;

import ec.gob.sri.comprobantes.api.EstadoAutorizacion;
import ec.gob.sri.comprobantes.dto.AutorizacionDTO;
import ec.gob.sri.comprobantes.exception.ConvertidorXMLException;
import ec.gob.sri.comprobantes.util.AutorizacionComprobantesUtil;
import ec.gob.sri.comprobantes.util.AutorizacionComprobantesWs;
import ec.gob.sri.comprobantes.util.xml.Java2XML;
import ec.gob.sri.comprobantes.util.xml.XStreamUtil;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.EnumFormatoFirma;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;

public class ElectronicVoucherHelper {

	/**
	 *
	 * ================== TABLA 3 TIPOS DE COMPROBANTES ======================
	 * FACTURA 01 NOTA DE CRÉDITO 04 NOTA DE DÉBITO 05 GUÍA DE REMISIÓN 06
	 * COMPROBANTE DE RETENCIÓN 07
	 * =============================================================================
	 */
	
	public class ElectronicVoucherResponce {

        private Document xmlDocument;
        private int result;
        private String message;
        
		public ElectronicVoucherResponce(Document xmlDocument, int result, String message) {
			super();
			this.xmlDocument = xmlDocument;
			this.result = result;
			this.message = message;
		}
		public Document getXmlDocument() {
			return xmlDocument;
		}
		public void setXmlDocument(Document xmlDocument) {
			this.xmlDocument = xmlDocument;
		}
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
        
        
    }
	
	public String numberComplete(String valueStr, Integer numberDigits){
        Integer value = Integer.parseInt(valueStr);        
        return String.format("%0"+numberDigits+"d", value);
    }
	
	public Document getRetentionPurchaseXMLDocument(Account account) throws Exception{
		
		String electronicVoucherenviroment = ParameterHelper.getValue("ELECTRONIC_VOUCHER_ENVIROMENT");
		AccountRetention accountRetention = XPersistence.getManager().find(AccountRetention.class, account.getAccountId());
		Person issuer = CompanyHelper.getDefaultPerson();
		
		Document xmlDocument;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		xmlDocument = documentBuilder.newDocument();
		xmlDocument.setXmlStandalone(true);
		
        Element comprobanteRetencion = xmlDocument.createElement("comprobanteRetencion");
        comprobanteRetencion.setAttribute("id", "comprobante");
        comprobanteRetencion.setAttribute("version", "1.0.0");

        xmlDocument.appendChild(comprobanteRetencion);
        
        Element infoTributaria = xmlDocument.createElement("infoTributaria");
        comprobanteRetencion.appendChild(infoTributaria);

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        // infoTributaria
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        infoTributaria.appendChild(addElement(xmlDocument, "ambiente", electronicVoucherenviroment));
        infoTributaria.appendChild(addElement(xmlDocument, "tipoEmision", "1"));
        
        //[0]FECHA_EMISION,
        //[1]TIPO_IDENTIFICACION
        //[2]NOMBRE_CLIENTE,
        //[3]IDENTIFICACION,
        //[4]DIRECCION_CLIENTE,         
        //[5]ESTABLECIMIENTO    
        //[6]PUNTO_EMISION
        //[7]SECUENCIAL
        
        //PRODUCTO ==>102 Factura de Venta
        //PRODUCTO ==>103 Retencion
        //PRODUCTO ==>202 Factura de Compra
        
        infoTributaria.appendChild(addElement(xmlDocument, "razonSocial", issuer.getName()));
        infoTributaria.appendChild(addElement(xmlDocument, "nombreComercial", issuer.getName()));
        infoTributaria.appendChild(addElement(xmlDocument, "ruc", issuer.getIdentification()));
        
        // Fecha de constitucion de la empresa CRESAFE aaaammdd ==> 26092012
        infoTributaria.appendChild(addElement(xmlDocument, "claveAcceso", getAccesKey(issuer, accountRetention.getEstablishmentCode(), accountRetention.getEmissionPointCode(), accountRetention.getSequentialCode(), "07", electronicVoucherenviroment)));
        infoTributaria.appendChild(addElement(xmlDocument, "codDoc", "07"));// COMPROBANTE DE RETENCIÓN 07
        infoTributaria.appendChild(addElement(xmlDocument, "estab", numberComplete(accountRetention.getEstablishmentCode(),3)));        
        infoTributaria.appendChild(addElement(xmlDocument, "ptoEmi", numberComplete(accountRetention.getEmissionPointCode(),3)));
        infoTributaria.appendChild(addElement(xmlDocument, "secuencial", numberComplete(accountRetention.getSequentialCode(),9)));
        infoTributaria.appendChild(addElement(xmlDocument, "dirMatriz", issuer.getLegalPerson().getHomeMainStreet()));
        
        Element infoCompRetencion = xmlDocument.createElement("infoCompRetencion");
        comprobanteRetencion.appendChild(infoCompRetencion);
        
        infoCompRetencion.appendChild(addElement(xmlDocument, "fechaEmision", UtilApp.dateToString(accountRetention.getIssueDate(), "dd/MM/yyyy")));
        infoCompRetencion.appendChild(addElement(xmlDocument, "dirEstablecimiento", issuer.getLegalPerson().getHomeMainStreet()));
        //infoCompRetencion.appendChild(addElement(xmlDocument, "contribuyenteEspecial",""));
        infoCompRetencion.appendChild(addElement(xmlDocument, "obligadoContabilidad","SI"));
       
        if (accountRetention.getAccountInvoice().getPerson().getIdentificationType().getIdentificationTypeId().equals("RUC"))
        	infoCompRetencion.appendChild(addElement(xmlDocument, "tipoIdentificacionSujetoRetenido", "04"));
		else if (accountRetention.getAccountInvoice().getPerson().getIdentificationType().getIdentificationTypeId().equals("CED"))
			infoCompRetencion.appendChild(addElement(xmlDocument, "tipoIdentificacionSujetoRetenido", "05"));
		else if (accountRetention.getAccountInvoice().getPerson().getIdentificationType().getIdentificationTypeId().equals("PAS"))
			infoCompRetencion.appendChild(addElement(xmlDocument, "tipoIdentificacionSujetoRetenido", "06"));
		else
			throw new OperativeException("identification_type_must_be_RUC_CED_PAS)");
        
        infoCompRetencion.appendChild(addElement(xmlDocument, "razonSocialSujetoRetenido", accountRetention.getAccountInvoice().getAccount().getPerson().getName()));
        infoCompRetencion.appendChild(addElement(xmlDocument, "identificacionSujetoRetenido", accountRetention.getAccountInvoice().getAccount().getPerson().getIdentification()));
        
        infoCompRetencion.appendChild(addElement(xmlDocument, "periodoFiscal", UtilApp.dateToString(accountRetention.getIssueDate(), "MM/yyyy")));
        
        //DETALLES
        Element impuestos = xmlDocument.createElement("impuestos");
        comprobanteRetencion.appendChild(impuestos);
        String estab = numberComplete(accountRetention.getAccountInvoice().getEstablishmentCode(),3);        
        String ptoEmi = numberComplete(accountRetention.getAccountInvoice().getEmissionPointCode(),3);
        String secuencial = numberComplete(accountRetention.getAccountInvoice().getSequentialCode(),9);
        
        for (AccountRetentionDetail detail : accountRetention.getDetails()){
            Element impuesto = xmlDocument.createElement("impuesto");
            impuestos.appendChild(impuesto);
            impuesto.appendChild(addElement(xmlDocument, "codigo", detail.getRetentionConcept().getTypeRetention().equals("VAT")?"2":"1"));
            
            impuesto.appendChild(addElement(xmlDocument, "codigoRetencion", detail.getRetentionConcept().getRetentionConceptId()));
            impuesto.appendChild(addElement(xmlDocument, "baseImponible", parseValue(detail.getAmount())));
            Integer percentage = (detail.getTaxPercentage()).intValue();
            impuesto.appendChild(addElement(xmlDocument, "porcentajeRetener",percentage.toString()));
            impuesto.appendChild(addElement(xmlDocument, "valorRetenido", parseValue(detail.getFinalAmount())));
            
            impuesto.appendChild(addElement(xmlDocument, "codDocSustento", accountRetention.getAccountInvoice().getInvoiceTaxSupport().getInvoiceTaxSupportId()));
            
            String numberInvoicePurchases = estab+ptoEmi+secuencial;
                    
            impuesto.appendChild(addElement(xmlDocument, "numDocSustento", numberInvoicePurchases));
            impuesto.appendChild(addElement(xmlDocument, "fechaEmisionDocSustento", UtilApp.dateToString(accountRetention.getAccountInvoice().getIssueDate(), "dd/MM/yyyy")));
            
        }

        //Element infoAdicional = xmlDocument.createElement("infoAdicional");
       
        //comprobanteRetencion.appendChild(infoAdicional);
        
        System.out.println(XMLDocumentToString(xmlDocument));
        
        factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(XMLDocumentToString(xmlDocument)));
	    return builder.parse(is);
        
    }

	@SuppressWarnings("unchecked")
	public Document getInvoiceSaleXMLDocument(Account account) throws Exception {

		String electronicVoucherenviroment = ParameterHelper.getValue("ELECTRONIC_VOUCHER_ENVIROMENT");
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		Person issuer = CompanyHelper.getDefaultPerson();

		Document xmlDocument;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		xmlDocument = documentBuilder.newDocument();
		xmlDocument.setXmlStandalone(true);

		Element invoiceElement = xmlDocument.createElement("factura");
		invoiceElement.setAttribute("id", "comprobante");
		invoiceElement.setAttribute("version", "1.1.0");

		xmlDocument.appendChild(invoiceElement);

		Element tributaryInformationElement = xmlDocument.createElement("infoTributaria");
		invoiceElement.appendChild(tributaryInformationElement);

		tributaryInformationElement.appendChild(addElement(xmlDocument, "ambiente", electronicVoucherenviroment));
		tributaryInformationElement.appendChild(addElement(xmlDocument, "tipoEmision", "1"));

		tributaryInformationElement.appendChild(addElement(xmlDocument, "razonSocial", issuer.getName()));
		tributaryInformationElement.appendChild(addElement(xmlDocument, "nombreComercial", issuer.getName()));
		tributaryInformationElement.appendChild(addElement(xmlDocument, "ruc", issuer.getIdentification()));

		tributaryInformationElement.appendChild(
				addElement(xmlDocument, "claveAcceso", getAccesKey(issuer, accountInvoice.getEstablishmentCode(), accountInvoice.getEmissionPointCode(), accountInvoice.getSequentialCode(), "01", electronicVoucherenviroment)));
		tributaryInformationElement.appendChild(addElement(xmlDocument, "codDoc", "01"));
		tributaryInformationElement
				.appendChild(addElement(xmlDocument, "estab", accountInvoice.getEstablishmentCode()));
		tributaryInformationElement
				.appendChild(addElement(xmlDocument, "ptoEmi", accountInvoice.getEmissionPointCode()));
		tributaryInformationElement.appendChild(addElement(xmlDocument, "secuencial",
				String.format("%09d", Integer.parseInt(accountInvoice.getSequentialCode()))));
		tributaryInformationElement
				.appendChild(addElement(xmlDocument, "dirMatriz", issuer.getLegalPerson().getHomeMainStreet()));

		Element invoiceInformationElement = xmlDocument.createElement("infoFactura");
		invoiceElement.appendChild(invoiceInformationElement);

		invoiceInformationElement.appendChild(addElement(xmlDocument, "fechaEmision",
				UtilApp.dateToString(accountInvoice.getIssueDate(), "dd/MM/yyyy")));

		invoiceInformationElement.appendChild(
				addElement(xmlDocument, "dirEstablecimiento", issuer.getLegalPerson().getHomeMainStreet()));

		if (accountInvoice.getPerson().getIdentificationType().getIdentificationTypeId().equals("RUC"))
			invoiceInformationElement.appendChild(addElement(xmlDocument, "tipoIdentificacionComprador", "04"));
		else if (accountInvoice.getPerson().getIdentificationType().getIdentificationTypeId().equals("CED"))
			invoiceInformationElement.appendChild(addElement(xmlDocument, "tipoIdentificacionComprador", "05"));
		else if (accountInvoice.getPerson().getIdentificationType().getIdentificationTypeId().equals("PAS"))
			invoiceInformationElement.appendChild(addElement(xmlDocument, "tipoIdentificacionComprador", "06"));
		else
			throw new OperativeException("identification_type_must_be_RUC_CED_PAS)");

		invoiceInformationElement
				.appendChild(addElement(xmlDocument, "razonSocialComprador", accountInvoice.getPerson().getName()));

		invoiceInformationElement.appendChild(
				addElement(xmlDocument, "identificacionComprador", accountInvoice.getPerson().getIdentification()));

		if (accountInvoice.getPerson().getPersonType().getPersonTypeId().equals("NAT"))
			invoiceInformationElement.appendChild(addElement(xmlDocument, "direccionComprador",
					accountInvoice.getPerson().getNaturalPerson().getHomeMainStreet()));
		else
			invoiceInformationElement.appendChild(addElement(xmlDocument, "direccionComprador",
					accountInvoice.getPerson().getLegalPerson().getHomeMainStreet()));

		invoiceInformationElement
				.appendChild(addElement(xmlDocument, "totalSinImpuestos", accountInvoice.getSubtotal().setScale(2,RoundingMode.HALF_UP).toString()));

		invoiceInformationElement
				.appendChild(addElement(xmlDocument, "totalDescuento", accountInvoice.getDiscount().setScale(2,RoundingMode.HALF_UP).toString()));

		Element totalWithTaxElement = xmlDocument.createElement("totalConImpuestos");
		invoiceInformationElement.appendChild(totalWithTaxElement);

		List<TaxType> taxTypes = XPersistence.getManager().createQuery("SELECT o FROM TaxType o").getResultList();

		for (TaxType taxType : taxTypes) {
			List<Tax> taxes = XPersistence.getManager()
					.createQuery("SELECT o FROM Tax o WHERE taxType.taxTypeId = :taxTypeId AND o.taxId != :noTaxId")
					.setParameter("taxTypeId", taxType.getTaxTypeId()).setParameter("noTaxId", "IVA14").getResultList();

			for (Tax tax : taxes) {
				Element totalTaxElement = xmlDocument.createElement("totalImpuesto");
				totalWithTaxElement.appendChild(totalTaxElement);
				totalTaxElement.appendChild(addElement(xmlDocument, "codigo", taxType.getExternalCode()));
				totalTaxElement.appendChild(addElement(xmlDocument, "codigoPorcentaje", tax.getExternalCode()));

				BigDecimal amount = BigDecimal.ZERO;
				BigDecimal taxAmount = BigDecimal.ZERO;
				for (AccountInvoiceDetail detail : accountInvoice.getDetails()) {
					if (detail.getTax().getTaxId().equals(tax.getTaxId())) {
						amount = amount.add(detail.getAmount());
						taxAmount = taxAmount.add(detail.getTaxAmount());
					}
				}
				totalTaxElement.appendChild(addElement(xmlDocument, "baseImponible", amount.setScale(2,RoundingMode.HALF_UP).toString()));
				totalTaxElement.appendChild(addElement(xmlDocument, "valor", taxAmount.setScale(2,RoundingMode.HALF_UP).toString()));
			}
		}

		invoiceInformationElement.appendChild(addElement(xmlDocument, "propina", "0"));

		invoiceInformationElement
				.appendChild(addElement(xmlDocument, "importeTotal", accountInvoice.getCalculateTotal().toString()));

		invoiceInformationElement.appendChild(
				addElement(xmlDocument, "moneda", accountInvoice.getAccount().getCurrency().getCurrencyId()));

		Element paymentsElement = xmlDocument.createElement("pagos");
		invoiceInformationElement.appendChild(paymentsElement);
		Element paymentElement = xmlDocument.createElement("pago");
		paymentsElement.appendChild(paymentElement);
		paymentElement.appendChild(addElement(xmlDocument, "formaPago", "20"));
		paymentElement.appendChild(addElement(xmlDocument, "total", accountInvoice.getCalculateTotal().toString()));

		Element detailsElement = xmlDocument.createElement("detalles");
		invoiceElement.appendChild(detailsElement);

		List<AccountInvoiceDetail> accountInvoiceDetails = XPersistence.getManager()
				.createQuery("SELECT o FROM AccountInvoiceDetail o WHERE accountInvoice.accountId = :accountId")
				.setParameter("accountId", account.getAccountId()).getResultList();

		for (AccountInvoiceDetail accountInvoiceDetail : accountInvoiceDetails) {
			Element detailElement = xmlDocument.createElement("detalle");
			detailsElement.appendChild(detailElement);
			detailElement.appendChild(
					addElement(xmlDocument, "descripcion", accountInvoiceDetail.getAccountDetail().getName()));

			detailElement
					.appendChild(addElement(xmlDocument, "cantidad", parseValue(accountInvoiceDetail.getQuantity())));
			detailElement.appendChild(
					addElement(xmlDocument, "precioUnitario", parseValue(accountInvoiceDetail.getCompleteUnitPrice())));
			detailElement
					.appendChild(addElement(xmlDocument, "descuento", parseValue(accountInvoiceDetail.getDiscount())));
			detailElement.appendChild(
					addElement(xmlDocument, "precioTotalSinImpuesto", parseValue(accountInvoiceDetail.getAmount())));

			Element taxesElement = xmlDocument.createElement("impuestos");
			detailElement.appendChild(taxesElement);
			Element taxElement = xmlDocument.createElement("impuesto");
			taxesElement.appendChild(taxElement);
			taxElement.appendChild(
					addElement(xmlDocument, "codigo", accountInvoiceDetail.getTax().getTaxType().getExternalCode()));
			taxElement.appendChild(
					addElement(xmlDocument, "codigoPorcentaje", accountInvoiceDetail.getTax().getExternalCode()));
			taxElement.appendChild(
					addElement(xmlDocument, "tarifa", accountInvoiceDetail.getTax().getPercentage().toString()));
			taxElement
					.appendChild(addElement(xmlDocument, "baseImponible", accountInvoiceDetail.getAmount().setScale(2,RoundingMode.HALF_UP).toString()));
			taxElement.appendChild(addElement(xmlDocument, "valor", accountInvoiceDetail.getTaxAmount().setScale(2,RoundingMode.HALF_UP).toString()));

		}

		System.out.println(XMLDocumentToString(xmlDocument));

		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(XMLDocumentToString(xmlDocument)));
	    return builder.parse(is);
	    
	}

	private Document byteToXMLDocument(byte[] bytes)
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		    DocumentBuilder builder;
			
			builder = factory.newDocumentBuilder();
	    
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			return builder.parse(byteArrayInputStream);
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public ElectronicVoucherResponce authorizeXMLDocument(Account account, Document xmlDocumentSigned) throws Exception {

        String url = null;
        String electronicVoucherenviroment = ParameterHelper.getValue("ELECTRONIC_VOUCHER_ENVIROMENT");
        if (electronicVoucherenviroment.equals("1"))
        	url = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
        else
        	url = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
        
        try {
            RespuestaSolicitud respuestaSolicitudEnvio = new RespuestaSolicitud();
            RespuestaComprobante respuestaComprobante = null;
            AutorizacionDTO autorizacionDTO = null;

            String accesKey = getAccessKey(xmlDocumentSigned);
            String codDoc = getDocumentCode(xmlDocumentSigned);
            String tipoComprobante = codDoc.substring(1);
            String nombreArchivo = account.getName();

            respuestaSolicitudEnvio = SendVoucherWS.obtenerRespuestaEnvio(XMLDocumentToByteArray(xmlDocumentSigned), CompanyHelper.getDefaultPerson().getIdentification(), tipoComprobante, accesKey, url);
            
            System.out.println("Envio: "+respuestaSolicitudEnvio.getEstado());
            
            if (respuestaSolicitudEnvio.getEstado().equals("DEVUELTA")) {
            	System.out.println("Error: "+SendVoucherWS.obtenerMensajeRespuesta(respuestaSolicitudEnvio));
            	return new ElectronicVoucherResponce (mergeArchivos(XMLDocumentToByteArray(xmlDocumentSigned), Java2XML.convertirAXml(respuestaSolicitudEnvio)), 1, "DEVUELTA");
            }
                        
            if (electronicVoucherenviroment.equals("1"))
            	respuestaComprobante = new AutorizacionComprobantesWs("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl").llamadaWSAutorizacionInd(accesKey);
            else
            	respuestaComprobante = new AutorizacionComprobantesWs("https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl").llamadaWSAutorizacionInd(accesKey); 

            if (!respuestaComprobante.getAutorizaciones().getAutorizacion().isEmpty()) {
                AutorizacionComprobantesUtil autorizacionComprobantesUtil = new AutorizacionComprobantesUtil(respuestaComprobante, nombreArchivo);
                autorizacionDTO = autorizacionComprobantesUtil.obtenerEstadoAutorizaccion();
                
                byte[] archivoRespuestaAutorizacionXML = obtenerRepuestaAutorizacionXML(autorizacionDTO.getAutorizacion());
                if (EstadoAutorizacion.AUT.equals(autorizacionDTO.getEstadoAutorizacion())) {
                	return new ElectronicVoucherResponce(byteToXMLDocument(archivoRespuestaAutorizacionXML), 0, null);
                } else {
                    if (EstadoAutorizacion.NAU.equals(autorizacionDTO.getEstadoAutorizacion())) {
                    	return new ElectronicVoucherResponce(byteToXMLDocument(archivoRespuestaAutorizacionXML), 1, autorizacionDTO.getEstadoAutorizacion().getDescripcion()+", "+autorizacionDTO.getMensaje());
                    }
                    if (EstadoAutorizacion.PRO.equals(autorizacionDTO.getEstadoAutorizacion())) {
                    	return new ElectronicVoucherResponce(byteToXMLDocument(archivoRespuestaAutorizacionXML), 1, autorizacionDTO.getEstadoAutorizacion().getDescripcion());
                    }
                }
                
                System.out.println(autorizacionDTO.getEstadoAutorizacion().getDescripcion());
                
            } else {
                return new ElectronicVoucherResponce (xmlDocumentSigned, 1, EstadoAutorizacion.NPR.getDescripcion()+", El archivo no tiene autorizaciones relacionadas");
            }

        } catch (Exception ex) {
            System.out.println("Error al tratar de enviar el comprobante hacia el SRI:\n" + ex.getMessage());
            return new ElectronicVoucherResponce (xmlDocumentSigned, 1, EstadoAutorizacion.NPR.getDescripcion()+", Error al tratar de enviar el comprobante hacia el SRI:\n" + ex.getMessage());
        }
		return null; 
        
    }
	
	private static byte[] obtenerRepuestaAutorizacionXML(Autorizacion autorizacion)
            throws ConvertidorXMLException {
        try {
            XStream xstream = XStreamUtil.getRespuestaXStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
            setXMLCDATA(autorizacion);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xstream.toXML(autorizacion, writer);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new ConvertidorXMLException("Se produjo un error al convetir el archivo al formato XML", ex);
        }
    }
	
	private static void setXMLCDATA(Autorizacion autorizacion) {
        autorizacion.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");
    }
	
	public static Document mergeArchivos(byte[] comprobante, byte[] respuesta)
            throws Exception {
        return merge("*", new byte[][]{comprobante, respuesta});
    }

    private static Document merge(String exp, byte[]... archivosXML)
            throws Exception {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expression = xpath.compile(exp);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(archivosXML[0]);
            Document base = docBuilder.parse(byteArrayInputStream);
            Node results = (Node) expression.evaluate(base, XPathConstants.NODE);
            for (int i = 1; i < archivosXML.length; i++) {
                ByteArrayInputStream byteArrayInputStreamMerge = new ByteArrayInputStream(archivosXML[i]);
                Document merge = docBuilder.parse(byteArrayInputStreamMerge);
                Node nextResults = (Node) expression.evaluate(merge, XPathConstants.NODE);
                results.appendChild(base.importNode(nextResults, true));
            }
            return base;
        } catch (Exception ex) {
            throw new InternalException("Se produjo un error al adjuntar los resultados de la respuesta al comprobante enviado");
        }
    }
	
	public String XMLDocumentToString(Document xmlDocument)
			throws TransformerConfigurationException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		DOMSource source = new DOMSource(xmlDocument);
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String output = writer.getBuffer().toString();

		return output;
	}

	private String parseValue(BigDecimal value) {
		value = value.setScale(2, RoundingMode.HALF_UP);
		return value.toString();
	}

	private Element addElement(Document xmlDocument, String name, String value) {
		Element element = xmlDocument.createElement(name);
		element.setTextContent(value);
		return element;
	}

	private String getAccesKey(Person issuer, String establishmentCode, 
			String emissionPointCode, 
			String sequentialCode, 
			String voucherType, 
			String testOrProduction) {
		StringBuilder accessKey = new StringBuilder();
		SimpleDateFormat formater = new SimpleDateFormat("ddMMyyyy");
		accessKey.append(formater.format(new Date()));
		accessKey.append(voucherType);
		accessKey.append(issuer.getIdentification());
		accessKey.append(testOrProduction);
		accessKey.append(establishmentCode);
		accessKey.append(emissionPointCode);
		Integer sequentialCodeInteger = Integer.parseInt(sequentialCode);
		accessKey.append(String.format("%09d", sequentialCodeInteger));

		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		accessKey.append(new Random().nextInt(10));
		
		accessKey.append("1");

		int multiplicationValues[] = { 7, 6, 5, 4, 3, 2 };
		int acumulator = 0;
		int verificationKey;

		int x = 0;
		for (int y = 0; y < accessKey.length(); y++) {
			acumulator += Character.getNumericValue(accessKey.charAt(y)) * multiplicationValues[x];
			x++;
			if (x == 6) {
				x = 0;
			}
		}

		verificationKey = acumulator % 11;
		verificationKey = 11 - verificationKey;
		switch (verificationKey) {
		case 10:
			verificationKey = 0;
			break;
		case 11:
			verificationKey = 1;
			break;
		}
		accessKey.append(verificationKey); // DIGITO DE VERIFICACION
		return accessKey.toString();
	}

	public byte[] XMLDocumentToByteArray(Document xmlDocument) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(bos);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();

		DOMSource source = new DOMSource(xmlDocument);

		transformer.transform(source, result);
		byte[] array = bos.toByteArray();
		return array;
	}

	public Document signXMLDocument(Document xmlDocument, InputStream signature, String passSignature)
			throws CertificateException, IOException {
		
		KeyStore keyStore = getKeyStore(signature, passSignature);
		if (keyStore == null) {
			throw new InternalException("No se pudo obtener almacen de firma.");
		}
		String alias = getAlias(keyStore);

		X509Certificate certificate = null;
		try {
			certificate = (X509Certificate) keyStore.getCertificate(alias);
			if (certificate == null) {
				throw new InternalException("No existe ningún certificado para firmar.");
			}
		} catch (KeyStoreException e1) {
			throw new InternalException("Error: " + e1.getMessage());
		}

		PrivateKey privateKey = null;
		KeyStore tmpKs = keyStore;

		try {
			privateKey = (PrivateKey) tmpKs.getKey(alias, passSignature.toCharArray());
		} catch (UnrecoverableKeyException e) {
			throw new InternalException("No existe clave privada para firmar.");
		} catch (KeyStoreException e1) {
			throw new InternalException("No existe clave privada para firmar.");
		} catch (NoSuchAlgorithmException e2) {
			throw new InternalException("No existe clave privada para firmar.");
		}

		Provider provider = keyStore.getProvider();
		DataToSign dataToSign = createDataToSign(xmlDocument);
		FirmaXML sign = new FirmaXML();
		Document docSigned = null;

		try {
			Object[] res = sign.signFile(certificate, dataToSign, privateKey, provider);
			docSigned = (Document) res[0];
		} catch (Exception ex) {
			throw new InternalException("Error realizando la firma: " + ex.getMessage());
		}

		return docSigned;
	}

	private String getAlias(KeyStore keyStore) throws IOException {
		String alias = null;

		try {
			Enumeration<String> nombres = keyStore.aliases();
			while (nombres.hasMoreElements()) {
				String tmpAlias = (String) nombres.nextElement();
				if (keyStore.isKeyEntry(tmpAlias)) {
					alias = tmpAlias;
				}
			}
		} catch (KeyStoreException e) {
			throw new IOException("Error: " + e.getMessage());
		}
		return alias;
	}

	private KeyStore getKeyStore(InputStream signature, String passSignature) throws CertificateException, IOException {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("PKCS12");
			ks.load(signature, passSignature.toCharArray());
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new InternalException("Error: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new InternalException("Error: " + e.getMessage());
		}
		return ks;
	}

	private DataToSign createDataToSign(Document xmlDocument) {
		DataToSign dataToSign = new DataToSign();

		dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);

		dataToSign.setEsquema(XAdESSchemas.XAdES_132);
		dataToSign.setXMLEncoding("UTF-8");
		dataToSign.setEnveloped(true);
		dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "contenido comprobante", null,
				"text/xml", null));
		dataToSign.setParentSignNode("comprobante");
		dataToSign.setDocument(xmlDocument);

		return dataToSign;
	}
	
	private static Object getValueXML(Document xmlDocument, String expression, QName returnType)
            throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile(expression);
        return xPathExpression.evaluate(xmlDocument, returnType);
    }

    public static String getAccessKey(Document xmlDocument)
    		throws Exception {
        return (String) getValueXML(xmlDocument, "/*/infoTributaria/claveAcceso", XPathConstants.STRING);
    }

    private static String getDocumentCode(Document xmlDocument)
            throws Exception {
        return (String) getValueXML(xmlDocument, "/*/infoTributaria/codDoc", XPathConstants.STRING);
    }
    
    }
