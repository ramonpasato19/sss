package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * For accessing to the View from DWR. <p>
 * 
 * @author Javier Paniza
 */

public class View extends DWRBase {
	
	private static Log log = LogFactory.getLog(View.class);
	
	public static void setFrameClosed(HttpServletRequest request, String frameId, boolean closed) { 
		try {
			String [] id = frameId.split("_");
			if (!"ox".equals(id[0])) {
				// Bad format. This method relies in the id format by Ids class
				log.warn(XavaResources.getString("impossible_store_frame_status")); 
				return;
			}
			String application = id[1];
			String module = id[2];
			checkSecurity(request, application, module);
			Users.setCurrent(request);

			org.openxava.view.View view = (org.openxava.view.View) 
				getContext(request).get(application, module, "xava_view");
			view.setFrameClosed(frameId, closed);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_frame_status"), ex);
		}
	}
	
	public static void moveCollectionElement(HttpServletRequest request, String tableId, int from, int to) { 
		TableId id = new TableId(tableId, 0);
		if (!id.isValid()) {
			log.error(XavaResources.getString("impossible_store_collection_element_movement"));
			throw new XavaException("impossible_store_collection_element_movement");
		}
		checkSecurity(request, id.getApplication(), id.getModule());
		Users.setCurrent(request);		
		org.openxava.view.View view = getView(request, id.getApplication(), id.getModule());
		try {
			view.getSubview(id.getCollection()).moveCollectionElement(from, to);
			XPersistence.commit();
		}
		catch (Exception ex) {
			XPersistence.rollback();
			log.error(XavaResources.getString("impossible_store_collection_element_movement"), ex);
			throw new XavaException("impossible_store_collection_element_movement");
		}
	}	
	
	private static org.openxava.view.View getView(HttpServletRequest request, String application, String module) { 
		org.openxava.view.View view = (org.openxava.view.View)		
			getContext(request).get(application, module, "xava_view"); 
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		view.setRequest(request);
		return view;
	}

}
