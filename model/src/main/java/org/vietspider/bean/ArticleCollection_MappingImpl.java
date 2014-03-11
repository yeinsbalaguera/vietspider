package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class ArticleCollection_MappingImpl implements SerializableMapping<ArticleCollection> {

	private final static int code=12677476;

	public ArticleCollection create() {
		return new ArticleCollection();
	}

	public void toField(ArticleCollection object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("properties")) {
			Properties map = null;
			map = object.getProperties();
			if(map == null) map = new Properties();
			XML2Object.getInstance().mapProperties(map, null, null,  node);
			object.setProperties(map);
			return;
		}
		if(name.equals("list")) {
			List<Article> list = null;
			list = object.get();
			if(list == null) list = new ArrayList<Article>();
			XML2Object.getInstance().mapCollection(list, Article.class, node);
			object.set(list);
			return;
		}

	}

	public XMLNode toNode(ArticleCollection object) throws Exception {
		XMLNode node = new XMLNode("article-collection");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getProperties(), node, false, "properties", "item", null, null);
		mapper.addNode(object.get(), node, false, "list", "item", "org.vietspider.bean.Article");
		return node;
	}
}
