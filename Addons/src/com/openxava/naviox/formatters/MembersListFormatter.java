package com.openxava.naviox.formatters;

import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MembersListFormatter implements IFormatter {
	
	private static Log log = LogFactory.getLog(MembersListFormatter.class);
	
	public String format(HttpServletRequest request, Object object) throws Exception {
		if (Is.empty(object)) return "";
		MetaModel model = null;
		StringBuffer sb = new StringBuffer();
		for (String memberName: object.toString().split(",")) {
			if (model == null) {
				model = MetaModel.get(memberName);
				continue;
			}
			if (sb.length() > 0) sb.append(", ");
			try {
				MetaMember member = model.getMetaMember(memberName);
				sb.append(member.getLabel());	
			} catch (ElementNotFoundException ex) {
				log.warn(ex.getMessage()); 
			} 			
		}
		return sb.toString();
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
