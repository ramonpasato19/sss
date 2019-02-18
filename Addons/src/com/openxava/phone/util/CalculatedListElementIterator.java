package com.openxava.phone.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openxava.formatters.BooleanFormatter;
import org.openxava.model.meta.MetaProperty;
import org.openxava.util.Is;
import org.openxava.util.Maps;
import org.openxava.util.Messages;
import org.openxava.view.View;
import org.openxava.web.WebEditors;

/**
 * 
 * @author Jeromy Altuna
 */
public class CalculatedListElementIterator implements Iterator<ListElement> {
	
	private View view;
	private Messages errors;
	private HttpServletRequest request;
	
	private List<MetaProperty> listProperties;
	private List<Map<String, Object>> values;
		
	private int row = 0;	
	private int headerIndex = -1;
	private int subheaderIndex = -1;
	private BooleanFormatter booleanFormatter; 
	
	@SuppressWarnings("unchecked")
	public CalculatedListElementIterator(View view, HttpServletRequest request, Messages errors) {		
		this.view = view;
		this.request = request;
		this.errors = errors;
		
		this.listProperties = view.getMetaPropertiesList();
		this.values = view.getCollectionValues();
	}
	
	@Override
	public boolean hasNext() {
		return row < values.size();
	}

	@Override
	public ListElement next() {
		ListElement el = new ListElement();
		el.setHeader(obtainHeader(row)); 
		el.setSubheader(obtainSubheader(row)); 
		el.setContent(obtainContent(row));
		row++;
		return el;
	}
	
	private String obtainHeader(int row) {		
		return format(row, getHeaderIndex());
	}
	
	private String obtainSubheader(int row) {
		if (getSubheaderIndex() < 0) return "";
		return format(row, getSubheaderIndex());
	}	
	
	private String format(int row, int index) {
		MetaProperty  p = listProperties.get(index);
		Object value = Maps.getValueFromQualifiedName(values.get(row), p.getName());
		
		if (p.hasValidValues()) {
			return p.getValidValueLabel(value);
		}
		else if (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class)) {
			return getBooleanFormatter().format(null, value);
		}
		else {
			return WebEditors.format(request, p, value, errors, view.getViewName(), true);
		}
	}
	
	private BooleanFormatter getBooleanFormatter() {
		if (booleanFormatter == null) {
			booleanFormatter = new BooleanFormatter();
		}
		return booleanFormatter;
	}
	
	private int getHeaderIndex() {
		if (headerIndex < 0) {
			headerIndex = firstIndexForAny("name", "nombre", "title", "titulo", "description", "descripcion", "number", "numero", "id");
			if (headerIndex < 0 && !listProperties.isEmpty()) headerIndex = 0;
		}
		return headerIndex ;
	}
	
	private int getSubheaderIndex() {
		if (subheaderIndex < 0) {
			subheaderIndex = secondIndexForAny("name", "nombre", "title", "titulo", "description", "descripcion", "number", "numero", "id");
			if (subheaderIndex < 0 && listProperties.size() > 1) {
				if (getHeaderIndex() == 0) subheaderIndex = 1;
				else subheaderIndex = 0;
			}
		}
		return subheaderIndex;
	}
	
	private String obtainContent(int row) {
		StringBuffer result = new StringBuffer();
		for (int index = 0; index < listProperties.size(); index++) {
			if (index == getHeaderIndex() || index == getSubheaderIndex()) continue;
			MetaProperty p = listProperties.get(index);
			Object value = Maps.getValueFromQualifiedName(values.get(row), p.getName());
			if (!(value instanceof BigDecimal) && Is.empty(value)) continue; 
			if (value instanceof byte[]) continue;
			if (result.length() > 0) result.append(", ");
			result.append(p.getLabel());
			result.append(": ");
			result.append("<b>");
			result.append(format(row, index));
			result.append("</b>");
		}
		return result.toString();
	}
	
	private int firstIndexForAny(String ... properties) {
		return indexForAny(1, properties);
	}
	
	private int secondIndexForAny(String ... properties) {
		return indexForAny(2, properties);
	}

	private int indexForAny(int ordinal, String ... properties) {
		int timesFound = 0;
		for (String propertyName: properties) {
			int idx = 0;
			for (MetaProperty metaProperty: listProperties) {
				if (metaProperty.getQualifiedName().equals(propertyName)) {
					if (++timesFound == ordinal) {
						return idx;
					}
				}
				idx++;
			}
		}
		return -1;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException(); 

	}
}
