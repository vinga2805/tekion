package main.java.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import main.java.dao.MysqlDao;

public class CreateTableServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(LoginServlet.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MysqlDao mysqlDao = new MysqlDao();
		String userName = request.getParameter("username");
		List<String> awsCreds = mysqlDao.getAwsCred(userName);
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsCreds.get(0), awsCreds.get(1));
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
		DynamoDB dynamoDB = new DynamoDB(client);
		String status = "fail";
		if (request.getParameter("ftype").equals("custom")) {
			List<KeySchemaElement> listCl = new ArrayList<KeySchemaElement>();
			List<AttributeDefinition> listDT = new ArrayList<AttributeDefinition>();
			String tableName = request.getParameter("tableName");
			AttributeDefinition dt = null;
			KeySchemaElement ckey = null;
			if (request.getParameter("partitionDataType").equals("number")) {
				ckey = new KeySchemaElement(request.getParameter("partitionkey"), KeyType.HASH);
				dt = new AttributeDefinition(request.getParameter("partitionkey"), ScalarAttributeType.N);
			} else if (request.getParameter("partitionDataType").equals("string")) {
				ckey = new KeySchemaElement(request.getParameter("partitionkey"), KeyType.HASH);
				dt = new AttributeDefinition(request.getParameter("partitionkey"), ScalarAttributeType.S);
			} else if (request.getParameter("partitionDataType").equals("bytebuffer")) {
				ckey = new KeySchemaElement(request.getParameter("partitionkey"), KeyType.HASH);
				dt = new AttributeDefinition(request.getParameter("partitionkey"), ScalarAttributeType.B);
			}
			if (ckey != null) {
				listCl.add(ckey);
			}
			if (dt != null) {
				listDT.add(dt);
			}

			if (request.getParameter("sortingDataType").equals("number")) {
				ckey = new KeySchemaElement(request.getParameter("sortingkey"), KeyType.RANGE);
				dt = new AttributeDefinition(request.getParameter("sortingkey"), ScalarAttributeType.N);
			} else if (request.getParameter("sortingDataType").equals("string")) {
				ckey = new KeySchemaElement(request.getParameter("sortingkey"), KeyType.RANGE);
				dt = new AttributeDefinition(request.getParameter("sortingkey"), ScalarAttributeType.S);
			} else if (request.getParameter("sortingDataType").equals("bytebuffer")) {
				ckey = new KeySchemaElement(request.getParameter("sortingkey"), KeyType.RANGE);
				dt = new AttributeDefinition(request.getParameter("sortingkey"), ScalarAttributeType.B);
			}

			if (ckey != null) {
				listCl.add(ckey);
			}
			if (dt != null) {
				listDT.add(dt);
			}

			List<String> messageList = createTableCustom(listCl, listDT, tableName, dynamoDB);
			logger.info(messageList);
			if (messageList.get(0).equals("Table created successfully")) {
				status = mysqlDao.addTable(userName, messageList.get(1));
			}
		} else {
			String jsonData = request.getParameter("jsonTemplate");
			List<String> messageList = createTable(jsonData, dynamoDB);
			logger.info(messageList);
			if (messageList.get(0).equals("Table created successfully")) {
				status = mysqlDao.addTable(userName, messageList.get(1));
			}
		}
		response.sendRedirect("Home.jsp?status=" + status);
	}

	private List<String> createTableCustom(List<KeySchemaElement> listCl, List<AttributeDefinition> listDT,
			String tableName, DynamoDB dynamoDB) {
		List<String> output = new ArrayList<String>();
		try {
			logger.info("Creating the table: " + tableName + ", wait...");
			Table table = dynamoDB.createTable(tableName, listCl, listDT, new ProvisionedThroughput(10L, 10L));
			table.waitForActive();
			output.add("Table created successfully");
			output.add(tableName);
		} catch (Exception e) {
			output.add(e.getMessage());
		}
		return output;
	}

	private static List<String> createTable(String json, DynamoDB dynamoDB) {
		List<String> output = new ArrayList<String>();
		try {
			String tableName = "";
			List<KeySchemaElement> listCl = new ArrayList<KeySchemaElement>();
			List<AttributeDefinition> listDT = new ArrayList<AttributeDefinition>();
			JSONArray jsonRules = new JSONArray(json);
			AttributeDefinition dt = null;
			KeySchemaElement ckey = null;
			for (int i = 0; i < jsonRules.length(); i++) {
				JSONObject obj = (JSONObject) jsonRules.get(i);
				tableName = obj.get("tableName").toString();
				JSONArray PartitionRules = new JSONArray(obj.get("partition").toString());
				for (int j = 0; j < PartitionRules.length(); j++) {
					JSONObject columnobj = (JSONObject) PartitionRules.get(j);
					String key = columnobj.getString("key");
					String dataType = columnobj.getString("dataType");
					if (dataType.equals("number")) {
						ckey = new KeySchemaElement(key, KeyType.HASH);
						dt = new AttributeDefinition(key, ScalarAttributeType.N);
					} else if (dataType.equals("string")) {
						ckey = new KeySchemaElement(key, KeyType.HASH);
						dt = new AttributeDefinition(key, ScalarAttributeType.S);
					} else if (dataType.equals("bytebuffer")) {
						ckey = new KeySchemaElement(key, KeyType.HASH);
						dt = new AttributeDefinition(key, ScalarAttributeType.B);
					}
					if (ckey != null) {
						listCl.add(ckey);
					}
					if (dt != null) {
						listDT.add(dt);
					}
				}
				JSONArray SortingRules = new JSONArray(obj.get("sorting").toString());
				for (int j = 0; j < SortingRules.length(); j++) {
					JSONObject columnobj = (JSONObject) SortingRules.get(j);
					String key = columnobj.getString("key");
					String dataType = columnobj.getString("dataType");
					if (dataType.equals("number")) {
						ckey = new KeySchemaElement(key, KeyType.RANGE);
						dt = new AttributeDefinition(key, ScalarAttributeType.N);
					} else if (dataType.equals("string")) {
						ckey = new KeySchemaElement(key, KeyType.RANGE);
						dt = new AttributeDefinition(key, ScalarAttributeType.S);
					} else if (dataType.equals("bytebuffer")) {
						ckey = new KeySchemaElement(key, KeyType.RANGE);
						dt = new AttributeDefinition(key, ScalarAttributeType.B);
					}
					if (ckey != null) {
						listCl.add(ckey);
					}
					if (dt != null) {
						listDT.add(dt);
					}
				}

				/*
				 * JSONArray columnjsonRules = new JSONArray(obj.get("keys").toString()); for
				 * (int j = 0; j < columnjsonRules.length(); j++) { JSONObject columnobj =
				 * (JSONObject) columnjsonRules.get(j); Iterator<String> keys =
				 * columnobj.keys(); while (keys.hasNext()) { logger.info((String) keys.next());
				 * System.out.println((String) keys.next()); String key = (String) keys.next();
				 * String value = columnobj.getString(key); AttributeDefinition dt = null;
				 * KeySchemaElement ckey = null; if (value.equals("number")) { ckey = new
				 * KeySchemaElement(key, KeyType.HASH); dt = new AttributeDefinition(key,
				 * ScalarAttributeType.N); } else if (value.equals("string")) { ckey = new
				 * KeySchemaElement(key, KeyType.RANGE); dt = new AttributeDefinition(key,
				 * ScalarAttributeType.S); } else if (value.equals("bytebuffer")) { ckey = new
				 * KeySchemaElement(key, KeyType.RANGE); dt = new AttributeDefinition(key,
				 * ScalarAttributeType.B); } if (ckey != null) { listCl.add(ckey); } if (dt !=
				 * null) { listDT.add(dt); } } }
				 */
			}
			logger.info("Creating the table: " + tableName + ", wait...");
			Table table = dynamoDB.createTable(tableName, listCl, listDT, new ProvisionedThroughput(10L, 10L));
			table.waitForActive();
			output.add("Table created successfully");
			output.add(tableName);
		} catch (Exception e) {
			output.add(e.getMessage());
		}
		return output;
	}

}
