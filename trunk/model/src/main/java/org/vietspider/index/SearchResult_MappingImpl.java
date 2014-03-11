package org.vietspider.index;


import org.vietspider.bean.Article;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SearchResult_MappingImpl implements SerializableMapping<SearchResult> {

	private final static int code=2564342;

	public SearchResult create() {
		return new SearchResult();
	}

	public void toField(SearchResult object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("score")) {
			object.setScore(XML2Object.getInstance().toValue(float.class, value));
			return;
		}
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("desc")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("article")) {
			object.setArticle(XML2Object.getInstance().toObject(Article.class, node));
			return;
		}

	}

	public XMLNode toNode(SearchResult object) throws Exception {
		XMLNode node = new XMLNode("search-result");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getScore(), node, false, "score");
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, true, "title");
		mapper.addNode(object.getDesc(), node, true, "desc");
		mapper.addNode(object.getArticle(), node, false, "article");
		return node;
	}
}
