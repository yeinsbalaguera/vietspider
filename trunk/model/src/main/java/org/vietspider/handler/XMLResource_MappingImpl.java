package org.vietspider.handler;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class XMLResource_MappingImpl implements SerializableMapping<XMLResource> {

	private final static int code=27730402;

	public XMLResource create() {
		return new XMLResource();
	}

	public void toField(XMLResource object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("link")) {
			object.setLink(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(XMLResource object) throws Exception {
		XMLNode node = new XMLNode("resource");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getLink(), node, true, "link");
		mapper.addNode(object.getName(), node, false, "name");
		return node;
	}
}
