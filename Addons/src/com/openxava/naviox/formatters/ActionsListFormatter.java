package com.openxava.naviox.formatters;

import javax.servlet.http.*;

import org.openxava.controller.meta.*;
import org.openxava.formatters.*;
import org.openxava.util.*;

import static org.openxava.util.Strings.*;

/**
 * 
 * @author Javier Paniza
 */
public class ActionsListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (Is.empty(object)) return "";
		StringBuffer result = new StringBuffer();
		
		String [] actions = object.toString().split(",");
		concatPlainActions(actions, result);
		concatCollectionActions(actions, result);
		
		return result.toString();
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}
	
	private void concatPlainActions(String[] actions, StringBuffer result) {
		for (String action : actions) {
			if (action.contains(":")) continue;
			if (result.length() > 0) result.append(", ");
			result.append(MetaControllers.getMetaAction(action).getLabel());			
		}
	}
	
	private void concatCollectionActions(String[] actions, StringBuffer result) {
		String prefix = "";
		StringBuffer sb = new StringBuffer(); 
		for (String action : actions) {
			if (!action.contains(":")) continue;
			if (!prefix.equals(firstToken(action, ":"))) {
				prefix = firstToken(action, ":");
				if (sb.length() > 0) sb.append(", ");
				sb.append(Labels.get(prefix));
				sb.append(": ");
				sb.append(Labels.get(lastToken(action, ":")));
			} else {
				sb.append(" - ");
				sb.append(Labels.get(lastToken(action, ":")));
			}			
		}
		if (result.length() == 0) result.append(sb); 
		else if (sb.length() > 0) result.append(", ").append(sb);		
	}
}
