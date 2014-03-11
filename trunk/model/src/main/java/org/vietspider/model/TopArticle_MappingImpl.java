package org.vietspider.model;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class TopArticle_MappingImpl implements SerializableMapping<TopArticle> {

	private final static int code=12698353;

	public TopArticle create() {
		return new TopArticle();
	}

	public void toField(TopArticle object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("rels")) {
			List<String> list = null;
			list = object.getRels();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setRels(list);
			return;
		}

	}

	public XMLNode toNode(TopArticle object) throws Exception {
		XMLNode node = new XMLNode("top-article");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getRels(), node, false, "rels", "item", "java.lang.String");
		return node;
	}
}
