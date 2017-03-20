package com.openxava.naviox.util;

import java.sql.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * Set the catalog for the JDBC connection from the current JPA default schema. <p>
 * 
 * @since 5.6
 * @author Javier Paniza
 */

public class SetCatalogFromPersistenceSchemaConnectionRefiner implements IConnectionRefiner{

	public void refine(Connection con) throws Exception {
		con.setCatalog(XPersistence.getDefaultSchema());
	}

}
