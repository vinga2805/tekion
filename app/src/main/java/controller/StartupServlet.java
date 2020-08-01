package main.java.controller;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import main.java.dao.MysqlDao;

public class StartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(StartupServlet.class.getName());

	public void init() {
		logger.info("Application Started..");
		MysqlDao mysqlDao = new MysqlDao();
		mysqlDao.setupDatabase();
	}

}