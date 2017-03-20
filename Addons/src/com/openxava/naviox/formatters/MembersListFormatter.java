package com.openxava.naviox.formatters;

import javax.servlet.http.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MembersListFormatter implements IFormatter {

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
			MetaMember member = model.getMetaMember(memberName);
			sb.append(member.getLabel());
		}
		return sb.toString();
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
