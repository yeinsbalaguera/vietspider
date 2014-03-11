package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class ProductContent_MappingImpl implements SerializableMapping<ProductContent> {

	private final static int code=23853518;

	public ProductContent create() {
		return new ProductContent();
	}

	public void toField(ProductContent object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("content")) {
			object.setContent(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("price")) {
			object.setPrice(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(ProductContent object) throws Exception {
		XMLNode node = new XMLNode("product-content");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getContent(), node, false, "content");
		mapper.addNode(object.getPrice(), node, false, "price");
		mapper.addNode(object.getImage(), node, false, "image");
		return node;
	}
}
