package com.openxava.naviox.formatters;

import javax.servlet.http.*;

import org.openxava.formatters.*;

/**
 * 
 * @author Javier Paniza
 */

public class ArrayAsStringFormatter implements IMultipleValuesFormatter {

	public String[] format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return new String[0];
		return object.toString().split(",");
	}

	public Object parse(HttpServletRequest request, String[] strings) throws Exception {
		if (strings == null) return "";
		StringBuffer sb = new StringBuffer();
		for (String s: strings) {
			if (sb.length() > 0) sb.append(",");
			sb.append(s);
		}
		return sb.toString();
	}

}
