package com.sap.als.utils;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class EmfGenerator {

	public static enum DB_TYPE {
		DATASOURCE, MYSQL
	}

	public static final DB_TYPE DB = DB_TYPE.DATASOURCE;

	public static EntityManagerFactory initEntityManagerFactory(DataSource ds)
			throws ServletException {
		if (DB == DB_TYPE.DATASOURCE) {
			return initEMFForDataSource(ds);
		} else if (DB == DB_TYPE.MYSQL) {
			return initEMFForMySQL();
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EntityManagerFactory initEMFForDataSource(DataSource ds)
			throws ServletException {
		try {
			Map properties = new HashMap();
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			properties.put("eclipselink.ddl-generation", "create-tables");
			return Persistence.createEntityManagerFactory("als", properties);
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}
	
	public static Session MailInit()
	{
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return (Session) ctx.lookup("java:comp/env/mail/Session");
			
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EntityManagerFactory initEMFForMySQL() {
		Map properties = new HashMap();
		properties.put("eclipselink.ddl-generation", "create-tables");
		properties.put(PersistenceUnitProperties.JDBC_DRIVER,
				"com.mysql.jdbc.Driver");
		properties.put(PersistenceUnitProperties.JDBC_URL,
				"jdbc:mysql://localhost:3306/test1");
		properties.put(PersistenceUnitProperties.JDBC_USER, "root");
		properties.put(PersistenceUnitProperties.JDBC_PASSWORD, "admin");
		properties.put("eclipselink.ddl-generation.output-mode", "database");
		return Persistence.createEntityManagerFactory("als", properties);
	}

}
