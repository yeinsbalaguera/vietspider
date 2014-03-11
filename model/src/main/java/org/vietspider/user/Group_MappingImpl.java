package org.vietspider.user;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Group_MappingImpl implements SerializableMapping<Group> {

	private final static int code=18485233;

	public Group create() {
		return new Group();
	}

	public void toField(Group object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("group-name")) {
			object.setGroupName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("users")) {
			List<String> list = null;
			list = object.getUsers();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setUsers(list);
			return;
		}
		if(name.equals("categories")) {
			List<String> list = null;
			list = object.getWorkingCategories();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setWorkingCategories(list);
			return;
		}

	}

	public XMLNode toNode(Group object) throws Exception {
		XMLNode node = new XMLNode("group");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getGroupName(), node, false, "group-name");
		mapper.addNode(object.getUsers(), node, false, "users", "item", "java.lang.String");
		mapper.addNode(object.getWorkingCategories(), node, false, "categories", "item", "java.lang.String");
		return node;
	}
}
