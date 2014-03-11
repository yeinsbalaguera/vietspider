package org.vietspider.parser.rss2;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;


public class MetaLink_MappingImpl implements SerializableMapping<MetaLink> {

	private final static int code=7720611;

	public MetaLink create() {
		return new MetaLink();
	}

	public void toField(MetaLink object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("rel")) {
			object.setRel(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("href")) {
			object.setHref(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(MetaLink object) throws Exception {
		XMLNode node = new XMLNode("link");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		Attribute attr_rel = new Attribute("rel", Object2XML.getInstance().toString(object.getRel()));
		attrs.add(attr_rel);
		Attribute attr_type = new Attribute("type", Object2XML.getInstance().toString(object.getType()));
		attrs.add(attr_type);
		Attribute attr_href = new Attribute("href", Object2XML.getInstance().toString(object.getHref()));
		attrs.add(attr_href);
		return node;
	}
}
