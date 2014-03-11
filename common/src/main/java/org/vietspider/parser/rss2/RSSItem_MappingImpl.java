package org.vietspider.parser.rss2;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class RSSItem_MappingImpl implements SerializableMapping<RSSItem> {

	private final static int code=15755548;

	public RSSItem create() {
		return new RSSItem();
	}

	public void toField(RSSItem object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("description")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("pubDate")) {
			object.setTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(RSSItem object) throws Exception {
		XMLNode node = new XMLNode("item");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getDesc(), node, false, "description");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addNode(object.getTime(), node, false, "pubDate");
		return node;
	}
}
