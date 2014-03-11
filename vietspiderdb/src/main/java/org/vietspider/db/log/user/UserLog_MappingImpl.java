package org.vietspider.db.log.user;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class UserLog_MappingImpl implements SerializableMapping<UserLog> {

	private final static int code=2340291;

	public UserLog create() {
		return new UserLog();
	}

	public void toField(UserLog object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("user-name")) {
			object.setUserName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("action")) {
			object.setAction(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(long.class, value));
			return;
		}

	}

	public XMLNode toNode(UserLog object) throws Exception {
		XMLNode node = new XMLNode("action-user");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getUserName(), node, false, "user-name");
		mapper.addPrimitiveNode(object.getAction(), node, false, "action");
		mapper.addPrimitiveNode(object.getTime(), node, false, "time");
		return node;
	}
}
