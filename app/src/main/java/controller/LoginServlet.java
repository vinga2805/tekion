package main.java.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import main.java.bean.LoginBean;
import main.java.dao.MysqlDao;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(LoginServlet.class.getName());

	public void init() {
		logger.info("Application Started..");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		LoginBean loginBean = new LoginBean();
		loginBean.setUserName(userName);
		loginBean.setPassword(password);
		MysqlDao mysqlDao = new MysqlDao();
		String userValidate = mysqlDao.authenticateUser(loginBean);
		if (userValidate.equals("SUCCESS")) {
			logger.info("Redirecting to home page");
			Cookie loginCookie = new Cookie("userName", userName);
			loginCookie.setMaxAge(30 * 60);
			response.addCookie(loginCookie);
			if (userName.equals("admin")) {
				response.sendRedirect("audit");
			} else {
				response.sendRedirect("home");
			}
		} else {
			logger.info("Erros in Login: " + userValidate);
			request.setAttribute("errMessage", userValidate);
			request.getRequestDispatcher("/login").forward(request, response);
		}
	}
}