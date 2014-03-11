package org.vietspider.db.search;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SEOData_MappingImpl implements SerializableMapping<SEOData> {

	private final static int code=27148830;

	public SEOData create() {
		return new SEOData();
	}

	public void toField(SEOData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("data")) {
			object.setData(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("update")) {
			object.setUpdate(XML2Object.getInstance().toValue(long.class, value));
			return;
		}

	}

	public XMLNode toNode(SEOData object) throws Exception {
		XMLNode node = new XMLNode("seo");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		mapper.addNode(object.getData(), node, false, "data");
		mapper.addPrimitiveNode(object.getUpdate(), node, false, "update");
		return node;
	}
}
