package org.vietspider.bean;


import org.vietspider.bean.TreeMenu2.SourceName;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SourceName_MappingImpl implements SerializableMapping<SourceName> {

	private final static int code=31090991;

	public SourceName create() {
		return new SourceName();
	}

	public void toField(SourceName object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("total")) {
			object.setTotal(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(SourceName object) throws Exception {
		XMLNode node = new XMLNode("source_name");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addPrimitiveNode(object.getTotal(), node, false, "total");
		return node;
	}
}
