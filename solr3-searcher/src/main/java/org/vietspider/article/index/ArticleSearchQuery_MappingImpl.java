package org.vietspider.article.index;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.token.attribute.Attributes;


public class ArticleSearchQuery_MappingImpl implements SerializableMapping<ArticleSearchQuery> {

	private final static int code=20198043;

	public ArticleSearchQuery create() {
		return new ArticleSearchQuery();
	}

	public void toField(ArticleSearchQuery object, XMLNode node, String name, String value) throws Exception {

	}

	public XMLNode toNode(ArticleSearchQuery object) throws Exception {
		XMLNode node = new XMLNode("article-search-query");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		return node;
	}
}
