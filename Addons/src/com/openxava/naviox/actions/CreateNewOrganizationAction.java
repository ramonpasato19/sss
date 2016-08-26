package com.openxava.naviox.actions;

import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class CreateNewOrganizationAction extends ViewBaseAction {
	
	private static Log log = LogFactory.getLog(CreateNewOrganizationAction.class);

	public void execute() throws Exception {
		Map values = getView().getValues();
		Messages errors = MapFacade.validate(getModelName(), values);
		if (errors.contains()) {
			addErrors(errors);
			return;
		}
		String name = (String) values.get("name");
		String schema = Organization.normalize(name);
		createSchema(schema);
		DB.createTenancy(schema);
		MapFacade.create(getModelName(), values);	
		Organization.resetCache();
		String url = getRequest().getContextPath() + "/o/" + schema; 
		addMessage(name + " " + XavaResources.getString("organization_created") + " <a href='" + url + "'>" + url + "</a>");
	}

	private void createSchema(String schema) throws SQLException {
		Connection con = DataSourceConnectionProvider.getByComponent("Organization").getConnection();
		PreparedStatement ps = null;
		try {
			String database = con.getMetaData().getDatabaseProductName().split(" ")[0];
			String sentenceTemplate = NaviOXPreferences.getInstance().getCreateSchema(database);
			String sentence = sentenceTemplate.replace("${schema}", schema);
			log.debug(XavaResources.getString("executing_on_database", database, sentence)); 
			ps = con.prepareStatement(sentence);
			ps.executeUpdate();
		}
		finally {
			if (ps != null) {
				try { ps.close(); } catch (Exception ex) {}
			}
			con.close();
		}
	}

}
