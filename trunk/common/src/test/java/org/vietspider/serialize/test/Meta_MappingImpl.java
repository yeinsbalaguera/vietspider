package org.vietspider.serialize.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Meta_MappingImpl implements SerializableMapping<Meta> {

	private final static int code=6090115;

	public Meta create() {
		return new Meta();
	}

	public void toField(Meta object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("list")) {
			List<String> list = null;
			list = object.getItem();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setItem(list);
			return;
		}
		if(name.equals("properties")) {
			Map<String,String> map = null;
			map = object.getProperties();
			if(map == null) map = new HashMap<String,String>();
			XML2Object.getInstance().mapProperties(map, String.class, String.class, node);
			object.setProperties(map);
			return;
		}
		if(name.equals("cdata-attrs")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			CDataAttrBean[] values = new CDataAttrBean[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(CDataAttrBean.class, n);
			}
			object.setCbeans(values);
			return;
		}

	}

	public XMLNode toNode(Meta object) throws Exception {
		XMLNode node = new XMLNode("meta");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getItem(), node, false, "list", "item", "java.lang.String");
		mapper.addNode(object.getProperties(), node, false, "properties", "property", "java.lang.String", "java.lang.String");
		mapper.addNode(object.getCbeans(), node, false, "cdata-attrs", "bean", "org.vietspider.serialize.test.CDataAttrBean");
		return node;
	}
}
