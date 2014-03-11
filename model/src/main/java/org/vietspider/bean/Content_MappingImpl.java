package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Content_MappingImpl implements SerializableMapping<Content> {

	private final static int code=27009115;

	public Content create() {
		return new Content();
	}

	public void toField(Content object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("meta")) {
			object.setMeta(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("text_content")) {
			object.setContent(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("status")) {
			object.setStatus(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(Content object) throws Exception {
		XMLNode node = new XMLNode("content");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getMeta(), node, false, "meta");
		mapper.addNode(object.getContent(), node, true, "text_content");
		mapper.addPrimitiveNode(object.getStatus(), node, false, "status");
		return node;
	}
}
