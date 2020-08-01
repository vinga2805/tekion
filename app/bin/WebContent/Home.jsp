<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Home Page</title>
</head>
<body>
	<center>
		<h2>Dashboard</h2>
	</center>
	<button type="button" onclick="alert('Hello world!')">Create
		Table</button>
	<button type="button" onclick="alert('Hello world!')">Create
		Table Using Template</button>
	<button type="button" onclick="alert('Hello world!')">All
		Tables</button>
	<div style="text-align: right">
		Welcome
		<%=request.getAttribute("userName")%>
		<a href="LogoutServlet"> Logout</a>
	</div>
	<div>
		<div id="template">
			<form>
				<textarea rows="20" cols="100"></textarea>
				<div style="padding-top: 10px">
					<input type="submit" value="Submit"></input>
				</div>
			</form>

		</div>
	</div>
</body>
</html>