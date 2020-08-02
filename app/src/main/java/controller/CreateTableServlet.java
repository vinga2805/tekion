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
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import main.java.dao.MysqlDao;

public class CreateTableServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(CreateTableServlet.class.getName());
	private ProjectionType projectedAttr = ProjectionType.ALL;
	private long readCapacity = 5L;
	private long writeCapacity = 5L;
	private boolean isIndex = false;
	private boolean isCapaity = true;
	private String nonkeyAttr = "";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MysqlDao mysqlDao = new MysqlDao();
		String userName = request.getParameter("username");
		String region = request.getParameter("region");
		List<String> awsCreds = mysqlDao.getAwsCred(userName);
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsCreds.get(0), awsCreds.get(1));
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
			if (!request.getParameter("sortingkey").isEmpty()) {
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
			}
			CreateTableRequest awsrequest = new CreateTableRequest().withTableName(tableName).withKeySchema(listCl)
					.withAttributeDefinitions(listDT);
			List<KeySchemaElement> indexkey = new ArrayList<KeySchemaElement>();
			String indexName = "";
			boolean isSec = false;
			if (request.getParameter("settingtype").equals("custom")) {
				projectedAttr = getProjectedAttr(request.getParameter("projattr"));
				isIndex = true;
				readCapacity = Long.parseLong(request.getParameter("readcapacity"));
				writeCapacity = Long.parseLong(request.getParameter("writecapacity"));
				indexName = request.getParameter("indexname");
				if (request.getParameter("nonkeyAttr") != null) {
					nonkeyAttr = request.getParameter("nonkeyAttr");
				}
				indexkey.add(new KeySchemaElement(request.getParameter("indexpartitionkey"), KeyType.HASH));
				if (!request.getParameter("indexsortingkey").isEmpty()) {
					isSec = true;
					indexkey.add(new KeySchemaElement(request.getParameter("indexsortingkey"), KeyType.RANGE));
				}
			}
			if (isCapaity) {
				awsrequest.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacity)
						.withWriteCapacityUnits(writeCapacity));
			}
			if (isIndex) {
				GlobalSecondaryIndex precipIndex = new GlobalSecondaryIndex().withIndexName(indexName)
						.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits((long) 10)
								.withWriteCapacityUnits((long) 1));
				if (!nonkeyAttr.isEmpty()) {
					precipIndex.withProjection(
							new Projection().withProjectionType(projectedAttr).withNonKeyAttributes(nonkeyAttr));
				} else {
					precipIndex.withProjection(new Projection().withProjectionType(projectedAttr));
				}
				ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
				indexKeySchema.add(indexkey.get(0)); // Partition key
				if (isSec) {
					indexKeySchema.add(indexkey.get(1)); // Sort key
				}
				precipIndex.setKeySchema(indexKeySchema);
				awsrequest.withGlobalSecondaryIndexes(precipIndex);
			}
			List<String> messageList = createTableCustom(tableName, awsrequest, credentials, region);
			logger.info(messageList);
			if (messageList.get(0).equals("Table created successfully")) {
				status = mysqlDao.addTable(userName, messageList.get(1));
			}
		} else {
			String jsonData = request.getParameter("jsonTemplate");
			List<String> messageList = createTable(jsonData, readCapacity, writeCapacity, projectedAttr, isIndex,
					isCapaity, nonkeyAttr, credentials);
			logger.info(messageList);
			if (messageList.get(0).equals("Table created successfully")) {
				status = mysqlDao.addTable(userName, messageList.get(1));
			}
		}
		response.sendRedirect("home?status=" + status);
	}

	private static Regions getRegion(String region) {
		Regions awsRegion = null;
		// TODO Auto-generated method stub
		switch (region) {
		case "us-east-2":
			awsRegion = Regions.US_EAST_2;
			break;
		case "us-east-1":
			awsRegion = Regions.US_EAST_1;
			break;
		case "us-west-1":
			awsRegion = Regions.US_WEST_1;
			break;
		case "us-west-2":
			awsRegion = Regions.US_WEST_2;
			break;
		case "ap-south-1":
			awsRegion = Regions.AP_SOUTH_1;
			break;
		case "ap-northeast-2":
			awsRegion = Regions.AP_NORTHEAST_2;
			break;
		case "ap-southeast-1":
			awsRegion = Regions.AP_SOUTHEAST_1;
			break;
		case "ap-southeast-2":
			awsRegion = Regions.AP_SOUTHEAST_2;
			break;
		case "ap-northeast-1":
			awsRegion = Regions.AP_NORTHEAST_1;
			break;
		case "ca-central-1":
			awsRegion = Regions.CA_CENTRAL_1;
			break;
		case "cn-north-1":
			awsRegion = Regions.CN_NORTH_1;
			break;
		case "eu-central-1":
			awsRegion = Regions.EU_CENTRAL_1;
			break;
		case "eu-west-1":
			awsRegion = Regions.EU_WEST_1;
			break;
		case "eu-west-2":
			awsRegion = Regions.EU_WEST_2;
			break;
		case "sa-east-1":
			awsRegion = Regions.SA_EAST_1;
			break;
		}
		return awsRegion;

	}

	private List<String> createTableCustom(String tableName, CreateTableRequest awsrequest,
			BasicAWSCredentials credentials, String region) {
		List<String> output = new ArrayList<String>();
		try {
			AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(getRegion(region));
			DynamoDB dynamoDB = new DynamoDB(client);
			logger.info(awsrequest.toString());
			logger.info("Creating the table: " + tableName + ", wait...");
			Table table = dynamoDB.createTable(awsrequest);
			table.waitForActive();
			output.add("Table created successfully");
			output.add(tableName);
		} catch (Exception e) {
			output.add(e.getMessage());
		}
		return output;
	}

	private static List<String> createTable(String json, long readCapacity, long writeCapacity,
			ProjectionType projectedAttr, boolean isIndex, boolean isCapaity, String nonkeyAttr,
			BasicAWSCredentials credentials) {

		List<KeySchemaElement> indexkey = new ArrayList<KeySchemaElement>();
		String indexName = "";
		List<String> output = new ArrayList<String>();
		try {
			String tableName = "";
			String region = "";
			String tableSetting = "default";
			List<KeySchemaElement> listCl = new ArrayList<KeySchemaElement>();
			List<AttributeDefinition> listDT = new ArrayList<AttributeDefinition>();
			JSONArray jsonRules = new JSONArray(json);
			AttributeDefinition dt = null;
			KeySchemaElement ckey = null;
			for (int i = 0; i < jsonRules.length(); i++) {
				JSONObject obj = (JSONObject) jsonRules.get(i);
				tableName = obj.get("tableName").toString();
				region = obj.get("region").toString();
				tableSetting = obj.get("tableSetting").toString();
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
				if (obj.has("sorting")) {
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
				}
				if (!tableSetting.equals("default")) {
					if (obj.get("globalindexes") != null) {
						isIndex = true;
						List<AttributeDefinition> indexdt = new ArrayList<AttributeDefinition>();
						JSONArray indexRules = new JSONArray(obj.get("globalindexes").toString());
						for (int j = 0; j < indexRules.length(); j++) {
							JSONObject columnobj = (JSONObject) indexRules.get(j);
							indexName = columnobj.getString("name");
							projectedAttr = getProjectedAttr(columnobj.getString("projectedAttr"));
							String partitionkey = "";
							if (columnobj.has("partitionkey")) {
								partitionkey = columnobj.getString("partitionkey");
							}
							String partitiondataType = "";
							if (columnobj.has("partitiondataType")) {
								partitiondataType = columnobj.getString("partitiondataType");
							}
							String sortingkey = "";
							if (columnobj.has("sortingkey")) {
								sortingkey = columnobj.getString("sortingkey");
							}
							String sortingdataType = "";
							if (columnobj.has("sortingdataType")) {
								sortingdataType = columnobj.getString("sortingdataType");
							}
							if (columnobj.has("nonkeyAttr")) {
								nonkeyAttr = columnobj.getString("nonkeyAttr");
							}
							if (!partitionkey.isEmpty() && partitionkey != null) {
								KeySchemaElement pkey = new KeySchemaElement(partitionkey, KeyType.HASH);
								AttributeDefinition pdt = new AttributeDefinition(partitionkey,
										getKeyType(partitiondataType));
								indexkey.add(pkey);
								indexdt.add(pdt);
							}
							if (!sortingkey.isEmpty() && sortingkey != null) {
								KeySchemaElement skey = new KeySchemaElement(sortingkey, KeyType.RANGE);
								AttributeDefinition sdt = new AttributeDefinition(sortingdataType,
										getKeyType(partitiondataType));
								indexkey.add(skey);
								indexdt.add(sdt);
							}
						}
					}

					if (obj.get("capacity") != null) {
						isCapaity = true;
						JSONArray capacityRule = new JSONArray(obj.get("capacity").toString());
						for (int j = 0; j < capacityRule.length(); j++) {
							JSONObject columnobj = (JSONObject) capacityRule.get(j);
							readCapacity = Long.parseLong(columnobj.getString("read"));
							writeCapacity = Long.parseLong(columnobj.getString("write"));
						}

					}
				}

			}
			AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(getRegion(region));
			DynamoDB dynamoDB = new DynamoDB(client);
			CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(listCl)
					.withAttributeDefinitions(listDT);
			if (isCapaity) {
				request.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacity)
						.withWriteCapacityUnits(writeCapacity));
			}
			if (isIndex) {
				GlobalSecondaryIndex precipIndex = new GlobalSecondaryIndex().withIndexName(indexName)
						.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits((long) 10)
								.withWriteCapacityUnits((long) 1));
				if (!nonkeyAttr.isEmpty()) {
					precipIndex.withProjection(
							new Projection().withProjectionType(projectedAttr).withNonKeyAttributes(nonkeyAttr));
				} else {
					precipIndex.withProjection(new Projection().withProjectionType(projectedAttr));
				}
				ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
				indexKeySchema.add(indexkey.get(0)); // Partition key
				if (indexkey.size() > 1) {
					indexKeySchema.add(indexkey.get(1)); // Sort key
				}
				precipIndex.setKeySchema(indexKeySchema);
				request.withGlobalSecondaryIndexes(precipIndex);
			}
			logger.info(request.toString());
			logger.info("Creating the table: " + tableName + ", wait...");
			Table table = dynamoDB.createTable(request);
			table.waitForActive();
			output.add("Table created successfully");
			output.add(tableName);
		} catch (Exception e) {
			output.add(e.getMessage());
		}
		return output;
	}

	private static ProjectionType getProjectedAttr(String projectedAttr) {
		// TODO Auto-generated method stub
		ProjectionType attr = ProjectionType.ALL;
		if (projectedAttr.equalsIgnoreCase("include")) {
			attr = ProjectionType.INCLUDE;
		} else if (projectedAttr.equalsIgnoreCase("key_only")) {
			attr = ProjectionType.KEYS_ONLY;
		}
		return attr;
	}

	private static ScalarAttributeType getKeyType(String partitiondataType) {
		ScalarAttributeType type = ScalarAttributeType.B;
		if (partitiondataType.equals("number")) {
			type = ScalarAttributeType.N;
		} else if (partitiondataType.equals("string")) {
			type = ScalarAttributeType.S;
		}
		return type;

	}

}
