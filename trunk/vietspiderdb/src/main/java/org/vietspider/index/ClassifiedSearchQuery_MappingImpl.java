package org.vietspider.index;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class ClassifiedSearchQuery_MappingImpl implements SerializableMapping<ClassifiedSearchQuery> {

	private final static int code=12041344;

	public ClassifiedSearchQuery create() {
		return new ClassifiedSearchQuery();
	}

	public void toField(ClassifiedSearchQuery object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("action")) {
			object.setAction(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(ClassifiedSearchQuery object) throws Exception {
		XMLNode node = new XMLNode("classified-search-query");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getAction(), node, false, "action");
		mapper.addNode(object.getSubQuery(), node, false, "sub-query");
		return node;
	}
}
