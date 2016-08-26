package com.openxava.naviox.actions;

import java.sql.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class DeleteOrganizationAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map values = getView().getValues();
		String schema = (String) values.get("id");
		deleteSchema(schema);
		MapFacade.remove(getModelName(), values);		
		addMessage("organization_deleted"); 		
	}

	private void deleteSchema(String schema) throws SQLException { 
		Connection con = DataSourceConnectionProvider.getByComponent("Organization").getConnection();
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("DROP SCHEMA " + schema + " CASCADE");
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
