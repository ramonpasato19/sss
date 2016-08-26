package com.powerfin.actions.report;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.model.*;

public class ReportSaveAction extends SaveAction{
	
	private static Log log = LogFactory.getLog(ReportSaveAction.class);
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void execute() throws Exception {
		super.execute();
		
		if (getErrors().isEmpty()) {
			
			String fileId = getView().getValueString("file");
			log.info("******* NEW FILE: "+fileId);
			
			File fileObject = (File)XPersistence.getManager().find(File.class, fileId);
			ArrayList<File> files = (ArrayList<File>) XPersistence.getManager().createQuery("select f from File f "
					+ "where f.name in (select f1.name from File f1 "
					+ "where f1.id = :fileId )"
					+ "and f.id != :fileId")
					.setParameter("fileId", fileId)
					.getResultList();
			if (files!=null && !files.isEmpty())
			{
				for(File f:files)
				{
					XPersistence.getManager().remove(f);
					log.info("******* REMOVE FILE (SOME NAME): "+f.getId());
				}
			}
		}
	}

}
