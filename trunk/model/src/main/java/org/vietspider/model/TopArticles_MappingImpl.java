package org.vietspider.model;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class TopArticles_MappingImpl implements SerializableMapping<TopArticles> {

	private final static int code=27523958;

	public TopArticles create() {
		return new TopArticles();
	}

	public void toField(TopArticles object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("article")) {
			List<TopArticle> list = null;
			list = object.getArticles();
			if(list == null) list = new ArrayList<TopArticle>();
			XML2Object.getInstance().mapCollection(list, TopArticle.class, node);
			object.setArticles(list);
			return;
		}

	}

	public XMLNode toNode(TopArticles object) throws Exception {
		XMLNode node = new XMLNode("top-articles");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getArticles(), node, false, "article", "item", "org.vietspider.model.TopArticle");
		return node;
	}
}
