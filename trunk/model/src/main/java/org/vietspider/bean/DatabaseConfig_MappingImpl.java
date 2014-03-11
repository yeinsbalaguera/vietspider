package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class DatabaseConfig_MappingImpl implements SerializableMapping<DatabaseConfig> {

	private final static int code=14093122;

	public DatabaseConfig create() {
		return new DatabaseConfig();
	}

	public void toField(DatabaseConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("host")) {
			object.setHost(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("port")) {
			object.setPort(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("database")) {
			object.setDatabase(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("username")) {
			object.setUsername(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("password")) {
			object.setPassword(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("deleteOldTable")) {
			object.setDeleteOldTable(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("createNewTable")) {
			object.setCreateNewTable(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(DatabaseConfig object) throws Exception {
		XMLNode node = new XMLNode("databaseConfig");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		mapper.addNode(object.getHost(), node, false, "host");
		mapper.addNode(object.getPort(), node, false, "port");
		mapper.addNode(object.getDatabase(), node, false, "database");
		mapper.addNode(object.getUsername(), node, false, "username");
		mapper.addNode(object.getPassword(), node, false, "password");
		mapper.addPrimitiveNode(object.isDeleteOldTable(), node, false, "deleteOldTable");
		mapper.addPrimitiveNode(object.isCreateNewTable(), node, false, "createNewTable");
		return node;
	}
}
