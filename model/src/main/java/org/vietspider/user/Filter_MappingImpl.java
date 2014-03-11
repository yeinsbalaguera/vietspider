package org.vietspider.user;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Filter_MappingImpl implements SerializableMapping<Filter> {

	private final static int code=5759024;

	public Filter create() {
		return new Filter();
	}

	public void toField(Filter object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("filter")) {
			object.setFilter(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(Filter object) throws Exception {
		XMLNode node = new XMLNode("filter");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getFilter(), node, false, "filter");
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		return node;
	}
}
