package org.vietspider.index;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SearchResponse_MappingImpl implements SerializableMapping<SearchResponse> {

	private final static int code=10573910;

	public SearchResponse create() {
		return new SearchResponse();
	}

	public void toField(SearchResponse object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("total")) {
			object.setTotal(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("articles")) {
			List<Article> list = null;
			list = object.getArticles();
			if(list == null) list = new ArrayList<Article>();
			XML2Object.getInstance().mapCollection(list, Article.class, node);
			object.setArticles(list);
			return;
		}
		if(name.equals("size")) {
			object.setSize(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("start")) {
			object.setStart(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("max")) {
			object.setMax(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(SearchResponse object) throws Exception {
		XMLNode node = new XMLNode("search-response");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getTotal(), node, false, "total");
		mapper.addPrimitiveNode(object.getTime(), node, false, "time");
		mapper.addNode(object.getArticles(), node, false, "articles", "item", "org.vietspider.bean.Article");
		mapper.addPrimitiveNode(object.getSize(), node, false, "size");
		mapper.addPrimitiveNode(object.getStart(), node, false, "start");
		mapper.addPrimitiveNode(object.getMax(), node, false, "max");
		mapper.addNode(object.getQuery(), node, false, "query");
		return node;
	}
}
