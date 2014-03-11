package org.vietspider.crawl.plugin.handler;


import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.RemoveAttribute;
import org.vietspider.html.Name;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class RemoveAttribute_MappingImpl implements SerializableMapping<RemoveAttribute> {

	private final static int code=25840096;

	public RemoveAttribute create() {
		return new RemoveAttribute();
	}

	public void toField(RemoveAttribute object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("tag")) {
			object.setName(XML2Object.getInstance().toValue(Name.class, value));
			return;
		}
		if(name.equals("attribute")) {
			object.setAttribute(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(RemoveAttribute object) throws Exception {
		XMLNode node = new XMLNode("item");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "tag");
		mapper.addNode(object.getAttribute(), node, false, "attribute");
		return node;
	}
}
