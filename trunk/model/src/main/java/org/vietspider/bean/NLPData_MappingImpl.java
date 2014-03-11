package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class NLPData_MappingImpl implements SerializableMapping<NLPData> {

	private final static int code=32271368;

	public NLPData create() {
		return new NLPData();
	}

	public void toField(NLPData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("values")) {
			List<String> list = null;
			list = object.getValues();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setValues(list);
			return;
		}

	}

	public XMLNode toNode(NLPData object) throws Exception {
		XMLNode node = new XMLNode("nld-data");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		mapper.addNode(object.getValues(), node, false, "values", "value", "java.lang.String");
		return node;
	}
}
