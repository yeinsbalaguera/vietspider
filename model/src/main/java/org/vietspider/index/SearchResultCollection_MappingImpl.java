package org.vietspider.index;


import java.util.ArrayList;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SearchResultCollection_MappingImpl implements SerializableMapping<SearchResultCollection> {

	private final static int code=27369926;

	public SearchResultCollection create() {
		return new SearchResultCollection();
	}

	public void toField(SearchResultCollection object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("total")) {
			object.setTotal(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("pattern")) {
			object.setPattern(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("collection")) {
			ArrayList<SearchResult> list = null;
			list = object.getCollection();
			if(list == null) list = new ArrayList<SearchResult>();
			XML2Object.getInstance().mapCollection(list, SearchResult.class, node);
			object.setCollection(list);
			return;
		}

	}

	public XMLNode toNode(SearchResultCollection object) throws Exception {
		XMLNode node = new XMLNode("search-result-collection");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getTotal(), node, false, "total");
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getPattern(), node, false, "pattern");
		mapper.addNode(object.getCollection(), node, false, "collection", "item", "org.vietspider.index.SearchResult");
		return node;
	}
}
