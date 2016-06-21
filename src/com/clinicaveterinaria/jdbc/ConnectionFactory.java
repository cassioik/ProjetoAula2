package com.clinicaveterinaria.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	  public Connection getConnection(){
	        try {
	        	return DriverManager.getConnection(
	        			"jdbc:hsqldb:hsql://localhost/clinicaveterinariadb",
	        			"SA", "");
	        } catch (SQLException e) {
	            throw new RuntimeException(e);//loga o erro
	        }
	   }
}
