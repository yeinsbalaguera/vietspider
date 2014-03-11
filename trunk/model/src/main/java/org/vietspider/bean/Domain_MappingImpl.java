package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Domain_MappingImpl implements SerializableMapping<Domain> {

	private final static int code=13755181;

	public Domain create() {
		return new Domain();
	}

	public void toField(Domain object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("data-type")) {
			object.setGroup(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("date")) {
			object.setDate(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("category")) {
			object.setCategory(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Domain object) throws Exception {
		XMLNode node = new XMLNode("Domain");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getGroup(), node, false, "data-type");
		mapper.addNode(object.getDate(), node, false, "date");
		mapper.addNode(object.getCategory(), node, false, "category");
		mapper.addNode(object.getName(), node, false, "name");
		return node;
	}
}
