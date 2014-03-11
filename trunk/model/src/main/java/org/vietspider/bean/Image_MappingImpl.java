package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Image_MappingImpl implements SerializableMapping<Image> {

	private final static int code=21679729;

	public Image create() {
		return new Image();
	}

	public void toField(Image object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("meta")) {
			object.setMeta(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Image object) throws Exception {
		XMLNode node = new XMLNode("image");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getMeta(), node, false, "meta");
		mapper.addNode(object.getType(), node, false, "type");
		mapper.addNode(object.getName(), node, false, "name");
		return node;
	}
}
