/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powerfin.util.externalWS;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import ec.gob.sri.comprobantes.modelo.Respuesta;
import ec.gob.sri.comprobantes.sql.RespuestaSQL;
import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

/**
 *
 * @author sistemas
 */
public class SendVoucherWS {
    
  private static RecepcionComprobantesOfflineService service;
  public static final String ESTADO_RECIBIDA = "RECIBIDA";
  public static final String ESTADO_DEVUELTA = "DEVUELTA";
  
  public SendVoucherWS(String wsdlLocation)
    throws MalformedURLException, WebServiceException
  {
    URL url = new URL(wsdlLocation);
    QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
    service = new RecepcionComprobantesOfflineService(url, qname);
  }
  
  public static final Object webService(String wsdlLocation)
  {
    try
    {
      QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
      URL url = new URL(wsdlLocation);
      service = new RecepcionComprobantesOfflineService(url, qname);
      return null;
    }
    catch (MalformedURLException ex1)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex1);
      return ex1;
    }
    catch (WebServiceException ex2)
    {
    	Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex2);
        return ex2;
    }
  }
  
  public RespuestaSolicitud enviarComprobante(String ruc, byte[] xmlDocumentByteArray, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try
    {
      RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
      response = port.validarComprobante(xmlDocumentByteArray);
    }
    catch (Exception e)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }
    return response;
  }
  /*
  public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte[] xml, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try
    {
      RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
      
      response = port.validarComprobante(xml);
    }
    catch (Exception e)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }
    return response;
  }
  
  public RespuestaSolicitud enviarComprobanteLotes(String ruc, File xml, String tipoComprobante, String versionXsd)
  {
    RespuestaSolicitud response = null;
    try
    {
      RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
      response = port.validarComprobante(ElectronicFileUtil.archivoToByte(xml));
    }
    catch (IOException e)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, e);
      response = new RespuestaSolicitud();
      response.setEstado(e.getMessage());
      return response;
    }
    return response;
  }
  */
  public static RespuestaSolicitud obtenerRespuestaEnvio(byte[] xmlDocumentByteArray, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl)
  {
    RespuestaSolicitud respuesta = new RespuestaSolicitud();
    SendVoucherWS cliente = null;
    try
    {
      cliente = new SendVoucherWS(urlWsdl);
    }
    catch (MalformedURLException ex1)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex1);
      respuesta.setEstado(ex1.getMessage());
      return respuesta;
    }
    catch (WebServiceException ex2)
    {
    	Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex2);
    	respuesta.setEstado(ex2.getMessage());
        return respuesta;
    }
    respuesta = cliente.enviarComprobante(ruc, xmlDocumentByteArray, tipoComprobante, "1.0.0");
    
    return respuesta;
  }
  
  public static RespuestaSolicitud obtenerRespuestaEnvio1(byte[] xmlDocumentByteArray, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl)
  {
    RespuestaSolicitud respuesta = new RespuestaSolicitud();
    SendVoucherWS cliente = null;
    try
    {
      cliente = new SendVoucherWS(urlWsdl);
    }
    catch (MalformedURLException ex1)
    {
      Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex1);
      respuesta.setEstado(ex1.getMessage());
      return respuesta;
    }
    catch (WebServiceException ex2)
    {
    	Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, ex2);
    	respuesta.setEstado(ex2.getMessage());
        return respuesta;
    }
    respuesta = cliente.enviarComprobante(ruc, xmlDocumentByteArray, tipoComprobante, "1.0.0");
    
    return respuesta;
  }
  
  public static void guardarRespuesta(String claveDeAcceso, String archivo, String estado, java.util.Date fecha)
  {

      java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());
      
      Respuesta item = new Respuesta(null, claveDeAcceso, archivo, estado, sqlDate);
      RespuestaSQL resp = new RespuestaSQL();
      try {
		resp.insertarRespuesta(item);
	} catch (ClassNotFoundException e) {
		Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, e);
	} catch (SQLException e) {
		Logger.getLogger(SendVoucherWS.class.getName()).log(Level.SEVERE, null, e);
	}

  }
  
  public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta)
  {
    StringBuilder mensajeDesplegable = new StringBuilder();
    if (respuesta.getEstado().equals("DEVUELTA") == true)
    {
      RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();
      for (Comprobante comp : comprobantes.getComprobante())
      {
        mensajeDesplegable.append(comp.getClaveAcceso());
        mensajeDesplegable.append("\n");
        for (Mensaje m : comp.getMensajes().getMensaje())
        {
          mensajeDesplegable.append(m.getMensaje()).append(" :\n");
          mensajeDesplegable.append(m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "");
          mensajeDesplegable.append("\n");
        }
        mensajeDesplegable.append("\n");
      }
    }
    return mensajeDesplegable.toString();
  }
}



