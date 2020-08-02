package main.java.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import main.java.bean.RegisterBean;
import main.java.dao.MysqlDao;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(LogoutServlet.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		String accesskey = request.getParameter("accesskey");
		String secretKey = request.getParameter("secretKey");

		RegisterBean registerBean = new RegisterBean();
		registerBean.setFullName(fullName);
		registerBean.setEmail(email);
		registerBean.setUserName(userName);
		registerBean.setPassword(password);
		registerBean.setAccesskey(accesskey);
		registerBean.setSecretkey(secretKey);

		MysqlDao mysqlDao = new MysqlDao();

		String userRegistered = mysqlDao.registerUser(registerBean);

		if (userRegistered.equals("SUCCESS")) // On success, you can display a message to user on Home page
		{
			response.sendRedirect("login");
			//request.getRequestDispatcher("/login").forward(request, response);
		} else // On Failure, display a meaningful message to the User.
		{
			request.setAttribute("errMessage", userRegistered);
			request.getRequestDispatcher("/register").forward(request, response);
		}
	}
}