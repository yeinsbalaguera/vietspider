package org.vietspider.bean.xml;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class XArticles_MappingImpl implements SerializableMapping<XArticles> {

	private final static int code=12677476;

	public XArticles create() {
		return new XArticles();
	}

	public void toField(XArticles object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("properties")) {
			Properties map = null;
			map = object.getProperties();
			if(map == null) map = new Properties();
			XML2Object.getInstance().mapProperties(map, null, null,  node);
			object.setProperties(map);
			return;
		}
		if(name.equals("list")) {
			List<XArticle> list = null;
			list = object.get();
			if(list == null) list = new ArrayList<XArticle>();
			XML2Object.getInstance().mapCollection(list, XArticle.class, node);
			object.set(list);
			return;
		}

	}

	public XMLNode toNode(XArticles object) throws Exception {
		XMLNode node = new XMLNode("xarticle");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getProperties(), node, false, "properties", "item", null, null);
		mapper.addNode(object.get(), node, false, "list", "item", "org.vietspider.bean.xml.XArticle");
		return node;
	}
}
