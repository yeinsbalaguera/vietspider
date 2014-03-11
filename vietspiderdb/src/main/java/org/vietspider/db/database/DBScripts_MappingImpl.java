package org.vietspider.db.database;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class DBScripts_MappingImpl implements SerializableMapping<DBScripts> {

	private final static int code=6557466;

	public DBScripts create() {
		return new DBScripts();
	}

	public void toField(DBScripts object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("inited")) {
			object.setInited(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(DBScripts object) throws Exception {
		XMLNode node = new XMLNode("database");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getInited(), node, false, "inited");
		mapper.addNode(object.getInitDB(), node, false, "scripts");
		return node;
	}
}
