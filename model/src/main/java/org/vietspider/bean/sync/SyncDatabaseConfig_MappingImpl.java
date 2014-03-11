package org.vietspider.bean.sync;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SyncDatabaseConfig_MappingImpl implements SerializableMapping<SyncDatabaseConfig> {

	private final static int code=5843889;

	public SyncDatabaseConfig create() {
		return new SyncDatabaseConfig();
	}

	public void toField(SyncDatabaseConfig object, XMLNode node, String name, String value) throws Exception {
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
		if(name.equals("auto")) {
			object.setAuto(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("driver")) {
			object.setDriver(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("connection")) {
			object.setConnection(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("load-categories-script")) {
			object.setLoadCategoriesScript(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("load-article-by-title")) {
			object.setLoadArticleByTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("scripts")) {
			List<String> list = null;
			list = object.getScripts();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setScripts(list);
			return;
		}

	}

	public XMLNode toNode(SyncDatabaseConfig object) throws Exception {
		XMLNode node = new XMLNode("syncDatabaseConfig");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		mapper.addNode(object.getHost(), node, false, "host");
		mapper.addNode(object.getPort(), node, false, "port");
		mapper.addNode(object.getDatabase(), node, false, "database");
		mapper.addNode(object.getUsername(), node, false, "username");
		mapper.addNode(object.getPassword(), node, false, "password");
		mapper.addPrimitiveNode(object.isAuto(), node, false, "auto");
		mapper.addNode(object.getDriver(), node, false, "driver");
		mapper.addNode(object.getConnection(), node, false, "connection");
		mapper.addNode(object.getLoadCategoriesScript(), node, false, "load-categories-script");
		mapper.addNode(object.getLoadArticleByTitle(), node, false, "load-article-by-title");
		mapper.addNode(object.getScripts(), node, false, "scripts", "script", "java.lang.String");
		return node;
	}
}
