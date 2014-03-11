package org.vietspider.bean;


import java.util.HashMap;
import java.util.Map;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Meta_MappingImpl implements SerializableMapping<Meta> {

	private final static int code=6615024;

	public Meta create() {
		return new Meta();
	}

	public void toField(Meta object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("des")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("source_time")) {
			object.setSourceTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("domain")) {
			object.setDomain(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("url")) {
			object.setSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Meta object) throws Exception {
		XMLNode node = new XMLNode("meta");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, true, "title");
		mapper.addNode(object.getDesc(), node, true, "des");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addNode(object.getTime(), node, false, "time");
		mapper.addNode(object.getProperties(), node, false, "properties", "property", "java.lang.String", "java.lang.String");
		mapper.addNode(object.getSourceTime(), node, false, "source_time");
		mapper.addNode(object.getDomain(), node, false, "domain");
		mapper.addNode(object.getSource(), node, false, "url");
		return node;
	}
}
