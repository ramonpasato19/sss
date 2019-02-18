package com.openxava.phone.web;

import org.openxava.web.style.*;

/**
 * For mobile devices. <p>
 * 
 * @author Javier Paniza
 */ 

public class PhoneStyle extends Style {
	
	private static PhoneStyle instance = null;	
	
	public PhoneStyle() {
	}
		
	public static Style getInstance() { 
		if (instance == null) {
			instance = new PhoneStyle();
		}
		return instance;
	}
	
	public boolean isForBrowse(String browser) {
		return browser != null && browser.endsWith("XAVAPHONE_BROWSER");
	}
		
	public boolean isSeparatorBeforeBottomButtons() {
		return false;
	}
	
	public boolean isOnlyOneButtonForModeIfTwoModes() {
		return true;
	}	
	


	public String getDetail() {
		return "";
	}
		
	public String getDefaultModeController() {
		return "Mode";
	}
	
	public boolean allowsResizeColumns() { 
		return false;
	}
	
	public boolean isRowLinkable() { 
		return false;
	}
	
	public boolean isShowPageNumber() { 
		return false;
	}
	
	public boolean isShowModuleDescription() { 
		return false;
	}
	
	public boolean isSeveralActionsPerRow() {
		return false;
	}
	
	public boolean isChangingPageRowCountAllowed() {
		return false;
	}
	
	public boolean isHideRowsAllowed() {
		return false;
	}
	
	public boolean isShowRowCountOnTop() {
		return true;
	}

	public boolean isUseLinkForNoButtonBarAction() { 
		return true;
	}
	
	public boolean isHelpAvailable()  { 
		return false;
	}
	
	public boolean isShowImageInButtonBarButton() {
		return false;
	}

	public boolean isUseStandardImageActionForOnlyImageActionOnButtonBar() {
		return true;
	}
	
	public boolean isFixedPositionSupported() {
		return true; 
	}
	
	public String getModuleSpacing() {
		return "style='padding: 0px;'";		
	}
			
	public String getListCellSpacing() {
		return "cellspacing=0 cellpadding=0";
	}
				
	public String getImagesFolder() { 
		return "phone/images";
	}
	
}

