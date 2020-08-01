package main.java.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class HealthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(HealthServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		Long startTime = System.currentTimeMillis();
		response.setContentType("application/json");
		logger.info("Called GET :: " + request.getServletPath());
		logger.info("Total Time Taken By GET Method : " + (System.currentTimeMillis() - startTime));
		out.print("This is working!!!");

	}

}