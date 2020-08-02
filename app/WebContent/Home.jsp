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

.lablediv {
	width: 160px;
	display: inline-block;
	text-align: right;
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
	function validatejson() {
		var jsonTemplate = document.formData.jsonTemplate.value;

		if (jsonTemplate == null || jsonTemplate == "") {
			alert("Json Template cannot be blank");
			return false;
		}
	}

	function validatecustom() {
		var tableName = document.formCustom.tableName.value;
		var partitionkey = document.formCustom.partitionkey.value;
		var settingtype = document.formCustom.settingtype.value;
		var region = document.formCustom.region.value;
		if (tableName == null || tableName == "" || tableName.length < 2) {
			alert("TableName must be at least 3 characters");
			return false;
		} else if (partitionkey == null || partitionkey == "") {
			alert("Partition key cannot be blank");
			return false;
		} else if (region == null || region == "") {
			alert("Aws region cannot be blank");
			return false;
		}
		if (settingtype == "custom") {
			var indexname = document.formCustom.indexname.value;
			var indexpartitionkey = document.formCustom.indexpartitionkey.value;
			if (indexname == null || indexname == "" || indexname.length < 2) {
				alert("Index Name must be at least 3 characters");
				return false;
			} else if (indexpartitionkey == null || indexpartitionkey == "") {
				alert("Index Partition key cannot be blank");
				return false;
			}
		}
	}

	function settingtoggle() {
		var checkBox = document.getElementById("setting");
		var customsetting = document.getElementById("customsetting");
		if (checkBox.checked == true) {
			customsetting.style.display = "none";
			document.formCustom.settingtype.value = "default";
		} else {
			customsetting.style.display = "block";
			document.formCustom.settingtype.value = "custom";
		}
	}
	function ShowHideDivNonAttr() {
		var select = document.getElementById("projattr");
		var nonkeyAttr = document.getElementById("nonkeyAttr");
		nonkeyAttr.style.display = select.value == "INCLUDE" ? "block" : "none";
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
			response.sendRedirect("login");
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
	<%=(userName.equals("admin"))
					? "<button type='button' onclick='showHide('tableDiv')'>Table Audit</button>"
					: ""%>
	<div style="text-align: right">
		Welcome
		<%=userName%>
		<a href="logout"> Logout</a>
	</div>
	<div>

		<div id="customTable" style="display: none;">

			<form name="formCustom" action="create" method="post"
				onsubmit="return validatecustom()">
				<input type="hidden" name="username" value="<%=userName%>" /> <input
					type="hidden" name="ftype" value="custom" />
				<div id="tableName">
					<label class="lablediv">Table Name(<span style="color: red">*</span>)
						:
					</label> <input type="text" name="tableName"
						style="margin-top: 10px; margin-bottom: 10px;">
				</div>
				<div id="awsregion">
					<label class="lablediv">Aws Region(<span style="color: red">*</span>)
						:
					</label><select id="region" name="region">
							<option value="">Select Aws Region</option>
							<option value="us-east-2">US East (Ohio)</option>
							<option value="us-east-1">US East (N. Virginia)</option>
							<option value="us-west-1">US West (N. California)</option>
							<option value="us-west-2">US West (Oregon)</option>
							<option value="ap-south-1">Asia Pacific (Mumbai)</option>
							<option value="ap-northeast-2">Asia Pacific (Seoul)</option>
							<option value="ap-southeast-1">Asia Pacific (Singapore)</option>
							<option value="ap-southeast-2">Asia Pacific (Sydney)</option>
							<option value="ap-northeast-1">Asia Pacific (Tokyo)</option>
							<option value="ca-central-1">Canada (Central)</option>
							<option value="cn-north-1">China (Beijing)</option>
							<option value="eu-central-1">Europe (Frankfurt)</option>
							<option value="eu-west-1">Europe (Ireland)</option>
							<option value="eu-west-2">Europe (London)</option>
							<option value="sa-east-1">South America (São Paulo)</option>
					</select></div>
				<div id="dynamicInput">
					<div>
						<label class="lablediv">Partition Key(<span
							style="color: red">*</span>) :
						</label> <input type="text" name="partitionkey"
							style="margin-top: 10px; margin-bottom: 10px;"> <select
							id="partitionDataType" name="partitionDataType">
							<option value="number">Number</option>
							<option value="string">String</option>
							<option value="bytebuffer">ByteBuffer</option>
						</select>
					</div>
					<div>
						<label class="lablediv">Sorting Key : </label> <input type="text"
							name="sortingkey" style="margin-top: 10px; margin-bottom: 10px;">
						<select id="sortingDataType" name="sortingDataType">
							<option value="number">Number</option>
							<option value="string">String</option>
							<option value="bytebuffer">ByteBuffer</option>
						</select>
					</div>
					<input type="hidden" name="settingtype" value="default" />
					<div>
						<label class="lablediv">Use default settings : </label> <input
							type="checkbox" id="setting" onclick="settingtoggle()" checked>
					</div>
					<div id="customsetting" style="display: none;">
						<div>
							<label class="lablediv">Index Name(<span
								style="color: red">*</span>) :
							</label> <input type="text" name="indexname"
								style="margin-top: 10px; margin-bottom: 10px;">
						</div>
						<div>
							<label class="lablediv">Index Partition Key(<span
								style="color: red">*</span>) :
							</label> <input type="text" name="indexpartitionkey"
								style="margin-top: 10px; margin-bottom: 10px;"> <select
								id="indexpartitionDataType" name="indexpartitionDataType">
								<option value="number">Number</option>
								<option value="string">String</option>
								<option value="bytebuffer">ByteBuffer</option>
							</select>
						</div>
						<div>
							<label class="lablediv">Index Sorting Key : </label> <input
								type="text" name="indexsortingkey"
								style="margin-top: 10px; margin-bottom: 10px;"> <select
								id="indexsortingDataType" name="indexsortingDataType">
								<option value="number">Number</option>
								<option value="string">String</option>
								<option value="bytebuffer">ByteBuffer</option>
							</select>
						</div>
						<div>
							<label class="lablediv">Projected attributes : </label> <select
								id="projattr" name="projattr" onchange="ShowHideDivNonAttr()">
								<option value="ALL">ALL</option>
								<option value="INCLUDE">INCLUDE</option>
								<option value="KEYS_ONLY">KEYS_ONLY</option>
							</select>

						</div>
						<div id="nonkeyAttr" style="display: none">
							<label class="lablediv">Non Key Attributes(<span
								style="color: red">*</span>) :
							</label> <input type="text" name="nonkeyAttr"
								style="margin-top: 10px; margin-bottom: 10px;">
						</div>

						<div>
							<label class="lablediv">Provisioned capacity : </label> <input
								type="number" name="readcapacity"
								style="margin-top: 10px; margin-bottom: 10px;"
								placeholder="Read Capacity"> <input type="number"
								name="writecapacity"
								style="margin-top: 10px; margin-bottom: 10px;"
								placeholder="Write Capacity">
						</div>
					</div>
				</div>
				<div style="padding-top: 10px">
					<input type="submit" value="Submit"></input>
				</div>
			</form>



		</div>

		<div id="template" style="display: none;">
			<form name="formData" action="create" method="post"
				onsubmit="return validatejson()">
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
	<div id="tableDiv"
		style='display:<%=(userName.equals("admin")) ? "none" : "block"%>'>
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
				<td colspan="3" style="text-align: center;">Tables Not
					Available</td>
			</tr>
			<%
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			%>
		</table>
	</div>
</body>
</html>