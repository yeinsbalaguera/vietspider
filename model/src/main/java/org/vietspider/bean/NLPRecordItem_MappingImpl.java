package org.vietspider.bean;


import java.util.HashMap;
import java.util.Map;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class NLPRecordItem_MappingImpl implements SerializableMapping<NLPRecordItem> {

	private final static int code=24438666;

	public NLPRecordItem create() {
		return new NLPRecordItem();
	}

	public void toField(NLPRecordItem object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("values")) {
			Map<String,String> map = null;
			map = object.getValues();
			if(map == null) map = new HashMap<String,String>();
			XML2Object.getInstance().mapProperties(map, String.class, String.class, node);
			object.setValues(map);
			return;
		}

	}

	public XMLNode toNode(NLPRecordItem object) throws Exception {
		XMLNode node = new XMLNode("nld-record-item");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getType(), node, false, "type");
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getValues(), node, false, "values", "value", "java.lang.String", "java.lang.String");
		return node;
	}
}
