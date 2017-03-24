package com.powerfin.integrations.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	
	private String host;
	private String user;
	private String password;
	private String database;
	private String driverName;
	private String url;
	
	private Connection connection;
	
	public ConnectionManager(String driverName, String url, String host, String user, String password, String database){
		this.host=host;
		this.user=user;
		this.password=password;
		this.database=database;
		this.driverName=driverName;
		this.url=url;
	}
	
	public boolean createConnection(){
		try{
			Class.forName(driverName);
			Connection connection = DriverManager.getConnection(url, user, password);
			connection.setAutoCommit(false);
			this.connection=connection;
		}catch(Exception e){
			System.out.println("Data Base Connection: "+e.getMessage());
		}
		return true;
	}
	
	public void commit() throws SQLException{
		connection.commit();
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void closeConnection() throws SQLException {
		
		this.connection.close();
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
