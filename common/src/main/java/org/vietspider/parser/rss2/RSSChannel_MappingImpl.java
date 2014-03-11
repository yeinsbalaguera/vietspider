package org.vietspider.parser.rss2;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class RSSChannel_MappingImpl implements SerializableMapping<RSSChannel> {

	private final static int code=14864257;

	public RSSChannel create() {
		return new RSSChannel();
	}

	public void toField(RSSChannel object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("generator")) {
			object.setGenerator(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(RSSChannel object) throws Exception {
		XMLNode node = new XMLNode("channel");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getGenerator(), node, false, "generator");
		return node;
	}
}
