package org.openxava.tools;

import java.sql.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.metamodel.*;

import org.apache.commons.logging.*;
import org.hibernate.cfg.*;
import org.hibernate.dialect.*;
import org.hibernate.internal.*;
import org.hibernate.tool.hbm2ddl.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 *
 * @since 5.3
 * @author Javier Paniza 
 */

public class SchemaTool {
	
	private static Log log = LogFactory.getLog(SchemaTool.class);	
	private boolean commitOnFinish = true;
	private Collection<Class> annotatedClasses = null;
	
	public static void main(String[] args) throws Exception { 
		if (args.length == 0 || Is.emptyString(args[0])) {
			log.error(XavaResources.getString("schematool_action_required")); 
			return;
		}				
		if (args.length == 1 || Is.emptyString(args[1])) {
			log.error(XavaResources.getString("schematool_persistence_unit_required")); 
			return;
		}	
		XPersistence.setPersistenceUnit(args[1]);
		SchemaTool tool = new SchemaTool();
		String action = args[0];
    	if (action.equals("update")) {
    		tool.updateSchema();
    	}
    	else if (action.equals("generate")) {
    		tool.printSchema();
    	}
    	else {
   			log.error(XavaResources.getString("schematool_action_required"));     		
    	}		
	}
	
	public void updateSchema() {
		execute(true, false);
	}
	
	public void generateSchema() {
		execute(false, false);
	}
	
	public void printSchema() {
		execute(false, true);
	}
	
	private void execute(boolean update, boolean console) {
		try {
			Configuration cfg = new Configuration();
			
			if (annotatedClasses != null) {
				for (Class annotatedClass: annotatedClasses) {
					cfg.addAnnotatedClass(annotatedClass);
				}						
			}
			else {
				cfg.addResource("GalleryImage.hbm.xml");
				for (ManagedType type: XPersistence.getManager().getMetamodel().getManagedTypes()) {
					Class<?> clazz = type.getJavaType();
					if (clazz == null || clazz.isInterface()) continue;
					cfg.addAnnotatedClass(clazz);
				}		
			}
			Properties props = new Properties();
			Map<String, Object> factoryProperties = XPersistence.getManager().getEntityManagerFactory().getProperties();
			String dialect = (String) factoryProperties.get("hibernate.dialect");
	    	props.put("hibernate.dialect", dialect);
	    	String schema = (String) factoryProperties.get("hibernate.default_schema"); 
	    	if (update) {
		    	java.sql.Connection connection = ((SessionImpl) XPersistence.getManager().getDelegate()).connection(); 
		    	DatabaseMetadata metadata = new DatabaseMetadata(connection, Dialect.getDialect(props), cfg); 
		    	List<SchemaUpdateScript> scripts = cfg.generateSchemaUpdateScriptList(Dialect.getDialect(props), metadata);
		    	XPersistence.commit();
		    	for (SchemaUpdateScript script: scripts) {
		    		String scriptWithSchema = addSchema(schema, script.getScript());
		    		log.info(XavaResources.getString("executing") + ": " + scriptWithSchema); 
		    		try {
						Query query = XPersistence.getManager().createNativeQuery(scriptWithSchema);
						query.executeUpdate();
						XPersistence.commit();
		    		}
		    		catch (Exception ex) {
		    			// In this case Hibernate logs the cause
		    			XPersistence.rollback();
		    		}
		    	}
	    	}
	    	else {
		    	String [] scripts = cfg.generateSchemaCreationScript(Dialect.getDialect(props));
				for (String rawScript: scripts) {
					String script = addSchema(schema, rawScript);
					if (console) {
						System.out.print(script); 
						System.out.println(';');
					}
					else {
						log.info(XavaResources.getString("executing") + ": " + script);
						Query query = XPersistence.getManager().createNativeQuery(script);
						query.executeUpdate();
					}
				}		
	    	}
		}
		catch (SQLException ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
		finally {
			if (commitOnFinish) XPersistence.commit();
		}		
	}
	
	private static String addSchema(String schema, String script) {
		if (Is.emptyString(schema)) return script;
		script = script.replaceAll("create table ", "create table " + schema + "."); 
		script = script.replaceAll("alter table ", "alter table " + schema + "."); 
		script = script.replaceAll("\\) references ", ") references " + schema + ".");
		script = script.replaceAll("create sequence ", "create sequence " + schema + ".");
		return script;
	}

	public boolean isCommitOnFinish() {
		return commitOnFinish;
	}

	public void setCommitOnFinish(boolean commitOnFinish) {
		this.commitOnFinish = commitOnFinish;
	}

	public void addAnnotatedClass(Class annotatedClass) {
		if (annotatedClasses == null) annotatedClasses = new ArrayList<Class>(); 
		annotatedClasses.add(annotatedClass);		
	}
	
}
