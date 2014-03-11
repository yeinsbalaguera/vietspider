package org.vietspider.parser.rss2;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class FeedItem_MappingImpl implements SerializableMapping<FeedItem> {

	private final static int code=30031746;

	public FeedItem create() {
		return new FeedItem();
	}

	public void toField(FeedItem object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("generator")) {
			object.setGenerator(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("author")) {
			object.setAuthor(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(FeedItem object) throws Exception {
		XMLNode node = new XMLNode("feed");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getGenerator(), node, false, "generator");
		mapper.addNode(object.getAuthor(), node, false, "author");
		return node;
	}
}
