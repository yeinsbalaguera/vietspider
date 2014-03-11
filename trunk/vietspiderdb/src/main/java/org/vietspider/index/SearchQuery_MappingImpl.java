package org.vietspider.index;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.token.attribute.Attributes;


public class SearchQuery_MappingImpl implements SerializableMapping<SearchQuery> {

	private final static int code=31144620;

	public SearchQuery create() {
		return new SearchQuery();
	}

	public void toField(SearchQuery object, XMLNode node, String name, String value) throws Exception {

	}

	public XMLNode toNode(SearchQuery object) throws Exception {
		XMLNode node = new XMLNode("search-query");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		return node;
	}
}
