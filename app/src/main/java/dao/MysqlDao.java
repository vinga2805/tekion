package main.java.dao;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.mindrot.jbcrypt.BCrypt;

import main.java.bean.LoginBean;
import main.java.bean.RegisterBean;
import main.java.util.DBConnection;

public class MysqlDao {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(MysqlDao.class.getName());

	public void setupDatabase() {
		Connection con = null;
		try {
			con = DBConnection.createConnection(false);
			ScriptRunner sr = new ScriptRunner(con);
			Reader reader = new FileReader(getClass().getClassLoader().getResource("tesla.sql").getFile());
			sr.runScript(reader);
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public String authenticateUser(LoginBean loginBean) {
		String userName = loginBean.getUserName(); // Assign user entered values to temporary variables.
		String password = loginBean.getPassword();

		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;

		String userNameDB = "";
		String passwordDB = "";

		try {
			con = DBConnection.createConnection(true);
			statement = con.createStatement();
			resultSet = statement.executeQuery("select userName,password from users");
			while (resultSet.next()) // Until next row is present otherwise it return false
			{
				userNameDB = resultSet.getString("userName"); // fetch the values present in database
				passwordDB = resultSet.getString("password");

				if (userName.equals(userNameDB) && checkPass(password, passwordDB)) {
					return "SUCCESS";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return "Invalid user credentials"; // Return appropriate message in case of failure

	}

	public String registerUser(RegisterBean registerBean) {
		String fullName = registerBean.getFullName();
		String email = registerBean.getEmail();
		String userName = registerBean.getUserName();
		String password = hashPassword(registerBean.getPassword());
		String accesskey = registerBean.getAccesskey();
		String secretkey = registerBean.getSecretkey();
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			con = DBConnection.createConnection(true);
			String query = "insert into users(id,fullName,email,userName,password,awsaccessKey,awssecreteKey) values (NULL,?,?,?,?,?,?)";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, fullName);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, userName);
			preparedStatement.setString(4, password);
			preparedStatement.setString(5, accesskey);
			preparedStatement.setString(6, secretkey);

			int i = preparedStatement.executeUpdate();

			if (i != 0) // Just to ensure data has been inserted into the database
				return "SUCCESS";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Oops.. Something went wrong there..!"; // On failure, send a message from here.
	}

	public List<String> getAwsCred(String userName) {
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String awsAccessKey = "";
		String awsSecreteKey = "";
		String awsregion = "";
		List<String> awsCred = new ArrayList<String>();

		try {
			con = DBConnection.createConnection(true);
			statement = con.createStatement();
			resultSet = statement.executeQuery(
					"select awsaccessKey,awssecreteKey from users where userName='" + userName + "'");
			while (resultSet.next()) // Until next row is present otherwise it return false
			{
				awsAccessKey = resultSet.getString("awsaccessKey"); // fetch the values present in database
				awsSecreteKey = resultSet.getString("awssecreteKey");
				awsCred.add(awsAccessKey);
				awsCred.add(awsSecreteKey);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return awsCred;

	}

	public String addTable(String userName, String tableName) {
		String status = "pass";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			con = DBConnection.createConnection(true);
			String query = "insert into tableList(name,createdBy) values (?,?)";
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, tableName);
			preparedStatement.setString(2, userName);
			int i = preparedStatement.executeUpdate();
			if (i != 0)
				return status;
		} catch (SQLException e) {
			status = "fail";
			e.printStackTrace();
		}
		return "pass";
	}

	public ResultSet getTableList(String userName) {
		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		String append = "";
		if (!userName.equals("admin") && !userName.isEmpty()) {
			append = " where createdBy='" + userName + "'";
		}
		try {
			con = DBConnection.createConnection(true);
			statement = con.createStatement();
			rs = statement.executeQuery("select * from tableList" + append + " order by createdAt asc");

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return rs;
	}

	private String hashPassword(String plainTextPassword) {
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}

	private boolean checkPass(String plainPassword, String hashedPassword) {
		if (BCrypt.checkpw(plainPassword, hashedPassword)) {
			return true;
		} else {
			return false;
		}

	}

}