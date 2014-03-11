package org.vietspider.model;


import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Groups_MappingImpl implements SerializableMapping<Groups> {

	private final static int code=30686709;

	public Groups create() {
		return new Groups();
	}

	public void toField(Groups object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("list")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Group[] values = new Group[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(Group.class, n);
			}
			object.setGroups(values);
			return;
		}

	}

	public XMLNode toNode(Groups object) throws Exception {
		XMLNode node = new XMLNode("groups");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getGroups(), node, false, "list", "", "org.vietspider.model.Group");
		return node;
	}
}
