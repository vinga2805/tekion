<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="main.java.dao.MysqlDao"%>
<%@page import="java.sql.ResultSet"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Audit Dashboard</title>
<style>
table {
	font-family: arial, sans-serif;
	border-collapse: collapse;
	width: 100%;
}

td, th {
	border: 1px solid #dddddd;
	text-align: left;
	padding: 8px;
}

tr:nth-child(even) {
	background-color: #dddddd;
}
</style>
</head>
<body>
	<%
		String userName = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userName"))
					userName = cookie.getValue();
			}
		}
		if (userName == null) {
			response.sendRedirect("login");
		}
	%>
	<%
		MysqlDao mysqlDao = new MysqlDao();
		ResultSet resultSet = mysqlDao.getTableList("");
	%>
	<center>
		<h2>Audit Dashboard</h2>
	</center>

	<div style="text-align: right">
		Welcome
		<%=userName%>
		<a href="logout"> Logout</a>
	</div>
	<h2>All Tables</h2>

	<table>
		<tr>
			<th>No.</th>
			<th>Table Name</th>
			<th>Created By</th>
			<th>Created At</th>
		</tr>
		<%
			try {
				int rowCount = 0;
				while (resultSet.next()) {
		%>
		<tr>
			<td><%=resultSet.getString("id")%></td>
			<td><%=resultSet.getString("name")%></td>
			<td><%=resultSet.getString("createdBy")%></td>
			<td><%=resultSet.getString("createdAt")%></td>
		</tr>
		<%
			rowCount++;
				}
				if (rowCount == 0) {
		%>
		<tr>
			<td colspan="4" style="text-align: center;">Tables Not Available</td>
		</tr>
		<%
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		%>
	</table>
</body>
</html>