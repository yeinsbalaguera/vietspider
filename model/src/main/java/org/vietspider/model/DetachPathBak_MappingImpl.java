package org.vietspider.model;


import java.util.List;
import java.util.Properties;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;


public class DetachPathBak_MappingImpl implements SerializableMapping<DetachPathBak> {

	private final static int code=26919651;

	public DetachPathBak create() {
		return new DetachPathBak();
	}

	public void toField(DetachPathBak object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("paths")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			String[] values = new String[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(String.class, n.getChild(0).getTextValue());
			}
			object.setPaths(values);
			return;
		}
		if(name.equals("properties")) {
			Properties map = null;
			map = object.getProperties();
			if(map == null) map = new Properties();
			XML2Object.getInstance().mapProperties(map, null, null,  node);
			object.setProperties(map);
			return;
		}

	}

	public XMLNode toNode(DetachPathBak object) throws Exception {
		XMLNode node = new XMLNode("region");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		Attribute attr_name = new Attribute("name", Object2XML.getInstance().toString(object.getName()));
		attrs.add(attr_name);
		mapper.addNode(object.getPaths(), node, true, "paths", "item", "java.lang.String");
		mapper.addNode(object.getProperties(), node, false, "properties", "property", null, null);
		return node;
	}
}
