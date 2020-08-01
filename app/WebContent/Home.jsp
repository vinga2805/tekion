<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="main.java.dao.MysqlDao"%>
<%@page import="java.sql.ResultSet"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Dashboard</title>
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
<script>
	function showHide(id) {
		var x = document.getElementById(id);
		document.getElementById('template').style.display = "none";
		document.getElementById('customTable').style.display = "none";
		if (x.style.display === "none") {
			x.style.display = "block";
		} else {
			x.style.display = "none";
		}

	}
</script>
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
			response.sendRedirect("Login.jsp");
		}
	%>
	<%
		MysqlDao mysqlDao = new MysqlDao();
		ResultSet resultSet = mysqlDao.getTableList(userName);
	%>
	<center>
		<h2>Dashboard</h2>
	</center>
	<div style="margin: 20px; text-align: center;">
		<span style="color: red"> <%=(request.getParameter("status") == null) ? ""
					: (request.getParameter("status").equals("fail")) ? "Problem with table creation"
							: "Table Created Successfully!"%></span>

	</div>
	<button type="button" onclick="showHide('customTable')">Create
		Table</button>
	<button type="button" onclick="showHide('template')">Create
		Table Using Template</button>
	<div style="text-align: right">
		Welcome
		<%=userName%>
		<a href="LogoutServlet"> Logout</a>
	</div>
	<div>

		<div id="customTable" style="display: none;">

			<form name="formCustom" action="CreateTableServlet" method="post">
				<input type="hidden" name="username" value="<%=userName%>" /> <input
					type="hidden" name="ftype" value="custom" />
				<div id="tableName">
					Table Name: <input type="text" name="tableName"
						style="margin-top: 10px; margin-bottom: 10px;">
				</div>
				<div id="dynamicInput">
					<div>
						Partition Key: <input type="text" name="partitionkey"
							style="margin-top: 10px; margin-bottom: 10px;"> <select
							id="partitionDataType" name="partitionDataType">
							<option value="number">Number</option>
							<option value="string">String</option>
							<option value="bytebuffer">ByteBuffer</option>
						</select>
					</div>
					<div>
						Sorting Key: <input type="text" name="sortingkey"
							style="margin-top: 10px; margin-bottom: 10px;"> <select
							id="sortingDataType" name="sortingDataType">
							<option value="number">Number</option>
							<option value="string">String</option>
							<option value="bytebuffer">ByteBuffer</option>
						</select>
					</div>
				</div>
				<div style="padding-top: 10px">
					<input type="submit" value="Submit"></input>
				</div>
			</form>



		</div>

		<div id="template" style="display: none;">
			<form name="formData" action="CreateTableServlet" method="post">
				<input type="hidden" name="ftype" value="template" /> <input
					type="hidden" name="username" value="<%=userName%>" />
				<textarea rows="20" cols="100" name="jsonTemplate"
					placeholder="Please add json template"></textarea>
				<div style="padding-top: 10px">
					<input type="submit" value="Submit"></input>
				</div>
			</form>

		</div>
	</div>
	<h2>Table List</h2>

	<table>
		<tr>
			<th>No.</th>
			<th>Table Name</th>
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
			<td><%=resultSet.getString("createdAt")%></td>
		</tr>

		<%
			rowCount++;
				}
				if (rowCount == 0) {
		%>
		<tr>
			<td colspan="3" style="text-align: center;">Tables Not Available</td>
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