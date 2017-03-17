package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * 
 * @since 4.8
 * @author Javier Paniza
 */

public class BooleanListFormatter implements IFormatter {
	
	public String format(HttpServletRequest request, Object booleanValue) {
		if (booleanValue == null) {
			return "";
		}
		boolean r = false;
		if (booleanValue instanceof Boolean) { 		
			r = ((Boolean) booleanValue).booleanValue();
			
		}
		else if (booleanValue instanceof Number) {
			r = ((Number) booleanValue).intValue() != 0;
		}
		else {
			return "";
		}
		String yes = Labels.get("yes", Locales.getCurrent());		
		String no = Labels.get("no", Locales.getCurrent()); 
		return r?"<div style='display: inline-block; width: 100%; text-align: center;'><i class='mdi mdi-check'></i><span style='display: none;'></div>" + yes + "</span>":"<span style='display: none;'>" + no + "</span>";
	}
	
	public Object parse(HttpServletRequest request, String string) {
		return null; // Not needed for list
	}

	
}
