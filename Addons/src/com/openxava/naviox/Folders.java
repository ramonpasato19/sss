package com.openxava.naviox;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class Folders implements java.io.Serializable {
	
	private static Log log = LogFactory.getLog(Folders.class);
	
	private Folder folder;
	private Modules modules;
	
	private boolean applicationNameAsRootLabel;  
	
	public String getFolderLabel() { 
		return getLabel(getFolder());
	}
	
	public String getParentFolderLabel() {
		if (getFolder() == null) return "";
		return getLabel(getFolder().getParent());
	}
	
	private String getLabel(Folder folder) {
		return folder==null?getRootLabel():folder.getLabel(); 
	}
	
	private String getRootLabel() { 
		return isApplicationNameAsRootLabel()?getApplicationLabel():Labels.get("home");
	}
	
	public String getApplicationLabel() { 
		return MetaApplications.getMetaApplication(MetaModuleFactory.getApplication()).getLabel();
	}

	public Folder getFolder() {
		if (folder != null && !XPersistence.getManager().contains(folder)) {
			if (folder.getId() == null) folder = null;
			else folder = XPersistence.getManager().merge(folder);
		}
		return folder;
	}
	
	public void goFolder(String oid) { 
		this.folder = Folder.find(oid);
	}
	
	public void goBack() { 
		this.folder = this.folder.getParent();
	}
	
	public boolean isRoot() { 
		return this.folder == null;
	}
	
	public Collection getSubfolders() { 
		return getSubfolders(getFolder());
	}
	
	public Collection getSubfolders(Folder folder) { 
		Collection subfolders = new ArrayList();
		for (Folder subfolder: Folder.findByParent(folder)) {
			if (!getFolderModules(subfolder).isEmpty() || !getSubfolders(subfolder).isEmpty()) {
				subfolders.add(subfolder);
			}
		}
		return subfolders;
	}

	
	public List getFolderModules() {
		return getFolderModules(getFolder());
	}
	
	private List getFolderModules(Folder folder) { 
		Collection<Module> folderModules = folder == null?Module.findInRoot():folder.getModules();
		List result = new ArrayList();
		for (Module module: folderModules) {
			if (!module.getApplication().equals(MetaModuleFactory.getApplication())) continue; // Because we can share the database schema by several applications
			MetaModule metaModule = MetaModuleFactory.create(module.getApplication(), module.getName());
			if (this.modules.isModuleAuthorized(metaModule)) {
				result.add(metaModule);
			}
		}
		return result;
	}

	public void setModules(Modules modules) {
		this.modules = modules;
	}

	public boolean isApplicationNameAsRootLabel() {
		return applicationNameAsRootLabel;
	}

	public void setApplicationNameAsRootLabel(boolean applicationNameAsRootLabel) {
		this.applicationNameAsRootLabel = applicationNameAsRootLabel;
	}

}
