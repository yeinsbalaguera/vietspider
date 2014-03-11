package org.vietspider.model;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class TopArticleCollection_MappingImpl implements SerializableMapping<TopArticleCollection> {

	private final static int code=19624427;

	public TopArticleCollection create() {
		return new TopArticleCollection();
	}

	public void toField(TopArticleCollection object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("collection-articles")) {
			List<TopArticles> list = null;
			list = object.getList();
			if(list == null) list = new ArrayList<TopArticles>();
			XML2Object.getInstance().mapCollection(list, TopArticles.class, node);
			object.setList(list);
			return;
		}

	}

	public XMLNode toNode(TopArticleCollection object) throws Exception {
		XMLNode node = new XMLNode("top-collection");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getList(), node, false, "collection-articles", "item", "org.vietspider.model.TopArticles");
		return node;
	}
}
