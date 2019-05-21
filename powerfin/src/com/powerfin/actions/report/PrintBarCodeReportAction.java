package com.powerfin.actions.report;

import java.util.HashMap;
import java.util.Map;

import com.powerfin.exception.OperativeException;
import com.powerfin.model.types.Types;
import com.powerfin.util.report.ReportBaseAction;

import net.sf.jasperreports.engine.JRDataSource;

public class PrintBarCodeReportAction extends ReportBaseAction{

	private String reportName;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Map getParameters() throws Exception {
		Integer numPrint = (Integer)getView().getValue("numPrint");
		String itemsProduct= (String)getView().getValue("itemsProduct");
		String itemProductFiltro=itemsProduct.replace(" ","");
		String []partsItemsProducts= itemProductFiltro.split(",");
		String itemProductConcat="";

		for( int i = 0; i < partsItemsProducts.length; i++)
		{
		    String element = partsItemsProducts[i];
		    String com= "'";
		    String com1="'";
		    String com2=",";
		    itemProductConcat+=com+element+com1+com2;
		    		
		}
		itemProductConcat = itemProductConcat.substring(0, itemProductConcat.length()-1);
		
		getView().getRoot().setValue("numPrint", numPrint);
		getView().getRoot().setValue("itemsProduct", itemsProduct);
			
		Map parameters = new HashMap();
		addDefaultParameters(parameters);
		parameters.put("NUM_IMPRESIONES", numPrint);
		parameters.put("ITEMS_PRODUCT", itemProductConcat);
		
		return parameters;
		
	}
	
	@Override
	protected JRDataSource getDataSource() throws Exception {
		return null;
	}

	@Override
	protected String getJRXML() throws Exception {
		return null;
	}

	@Override
	protected String getReportName() throws Exception {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getFormat() {
		Types.ReportFormat reportFormat = (Types.ReportFormat)getView().getValue("reportFormat");
		return reportFormat.toString();
	}

	public void validate()
	{
		Types.ReportFormat format = (Types.ReportFormat)getView().getValue("reportFormat");
		if (format==null)
			throw new OperativeException("format_is_required");
	}
}
