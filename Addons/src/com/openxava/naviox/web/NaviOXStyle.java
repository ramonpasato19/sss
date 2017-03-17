package com.openxava.naviox.web;

import javax.servlet.http.*;

import org.openxava.web.style.*;

/**
 * @author Javier Paniza
 */

public class NaviOXStyle extends Style {
	
	private static NaviOXStyle instance = null;

	public NaviOXStyle() {
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/smoothness/jquery-ui.css";
	}
	
	public static String getBodyClass(HttpServletRequest request) {
		String browser = request.getHeader("user-agent");
		if (browser == null) return "";
		if (browser.contains("Trident") || browser.contains("MSIE")) return "class='ie'"; 
		if (browser.contains("iPad")) return "class='ipad'";
		if (browser.contains("Firefox")) return "class='firefox'"; 
		return "";
	}

	
	public static Style getInstance() {
		if (instance == null) {
			instance = new NaviOXStyle();
		}
		return instance;
	}

	public String getHelpImage() {
		return null; 
	}
	
	public String getLoadingImage() {
		return "naviox/images/loading.gif"; 
	}
	
	/**
	 * @since 5.1.1
	 */
	public String getProcessingImage() { 
		return "naviox/images/processing.gif"; 
	}

	public String getMinimizeImage() {
		return null;
	}
	
	public String getRemoveImage() {
		return getImagesFolder() +  "/delete.gif"; 
	}

	@Override
	public String getRestoreImage() {
		return null; 
	}
	
	public String getFrame() { 
		return "ox-frame";
	}
	
	public String getModuleSpacing() {
		return "";		
	}
		
	/**
	 * @since 5.1.1
	 */
	public double getListAdjustment() { 
		return 17;		
	}
	
	/**
	 * @since 5.1.1
	 */
	public double getCollectionAdjustment() { 
		return -40;		
	}

	public String getFrameHeaderStartDecoration(int width) {  
		StringBuffer r = new StringBuffer();
		r.append("<div ");
		r.append(" class='");
		r.append(getFrame());
		r.append("'"); 
		if (width == 100) { 
			r.append(" style='width: calc(100% - 15px);'");			
		}
		else if (width == 50) { // Two collections in a row
			r.append(" style='overflow: auto; display: block; width: calc(50% - 29px);'"); 
		}
		r.append(getFrameSpacing());
		r.append(">");
		r.append("<div class='");
		r.append(getFrameTitle());
		r.append("'>");		
		r.append("\n");						
		return r.toString();
	}

		
	public String getFrameTitleStartDecoration() {
		StringBuffer r = new StringBuffer();
		r.append("<span ");
		r.append("class='");
		r.append(getFrameTitleLabel());
		r.append("'>\n");
		return r.toString();
	}
	
	public String getFrameActionsStartDecoration() {		
		return "<span class='" + getFrameActions() + "'>"; 
	}
	
	public String getFrameHeaderEndDecoration() {
		return "</div>";			
	}
	
	public String getFrameContentStartDecoration(String id, boolean closed) {
		StringBuffer r = new StringBuffer();
		r.append("<div id='");
		r.append(id);
		r.append("' ");
		if (closed) r.append("style='display: none;'");
		r.append("><div class='");
		r.append(getFrameContent());		
		r.append("'>\n");
		return r.toString();
	}
		
	public String getFrameContentEndDecoration() { 
		return "\n</div></div></div>"; 
	}
		
	public String getSectionBarStartDecoration() { 
		return "<td>";
	}
	
	public String getSectionBarEndDecoration() {
		return "</td>";
	}

	public String getErrorStartDecoration () {  
		return "<div class='ox-message-box'><i class='mdi mdi-close-octagon'></i>"; 
	}
	
	public String getErrorEndDecoration () { 
		return "</div>";
	}
	
	public String getMessageStartDecoration () {  
		return "<div class='ox-message-box'><i class='mdi mdi-checkbox-marked-circle'></i>"; 
	}
		
	public String getMessageEndDecoration () { 
		return "</div>";
	}
	
	/**
	 * @since 5.5
	 */
	public String getWarningStartDecoration() { 
		return "<div class='ox-message-box'><i class='mdi mdi-alert'></i>";
	}

	/**
	 * @since 5.5
	 */
	public String getInfoStartDecoration() { 
		return "<div class='ox-message-box'><i class='mdi mdi-information'></i>";
	}	

}
