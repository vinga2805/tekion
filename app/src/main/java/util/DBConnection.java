package main.java.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import main.java.controller.LogoutServlet;

public class DBConnection {
	public static Connection createConnection(boolean db) {
		final Logger logger = Logger.getLogger(LogoutServlet.class.getName());
		Connection con = null;
		String url = "";
		if (db) {
			url = "jdbc:mysql://" + System.getenv("MYSQLHOST") + ":" + System.getenv("MYSQLPORT") + "/"
					+ System.getenv("MYSQLDB") + "";
		} else {
			url = "jdbc:mysql://" + System.getenv("MYSQLHOST") + ":" + System.getenv("MYSQLPORT") + "/";
		}
		String username = System.getenv("MYSQLUNAME");
		String password = System.getenv("MYSQLPASS");
		logger.info("Connecting to mysql");
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				logger.info("Error in mysql connection: " + e.getMessage());
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url, username, password);
			logger.info("connection object: " + con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}