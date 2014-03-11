package org.vietspider.user;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class User_MappingImpl implements SerializableMapping<User> {

	private final static int code=1136752;

	public User create() {
		return new User();
	}

	public void toField(User object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("full-name")) {
			object.setFullName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("password")) {
			object.setPassword(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("username")) {
			object.setUserName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("email")) {
			object.setEmail(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("permission")) {
			object.setPermission(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("groups")) {
			List<String> list = null;
			list = object.getGroups();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setGroups(list);
			return;
		}

	}

	public XMLNode toNode(User object) throws Exception {
		XMLNode node = new XMLNode("user");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getFullName(), node, false, "full-name");
		mapper.addNode(object.getPassword(), node, false, "password");
		mapper.addNode(object.getUserName(), node, false, "username");
		mapper.addNode(object.getEmail(), node, false, "email");
		mapper.addPrimitiveNode(object.getPermission(), node, false, "permission");
		mapper.addNode(object.getGroups(), node, false, "groups", "item", "java.lang.String");
		return node;
	}
}
