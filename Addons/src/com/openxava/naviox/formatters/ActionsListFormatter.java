package com.openxava.naviox.formatters;

import javax.servlet.http.*;

import org.openxava.controller.meta.*;
import org.openxava.formatters.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class ActionsListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (Is.empty(object)) return "";
		StringBuffer sb = new StringBuffer();
		for (String action: object.toString().split(",")) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(MetaControllers.getMetaAction(action).getLabel());
		}
		return sb.toString();
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
