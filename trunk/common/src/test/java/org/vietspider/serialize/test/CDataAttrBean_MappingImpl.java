package org.vietspider.serialize.test;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;


public class CDataAttrBean_MappingImpl implements SerializableMapping<CDataAttrBean> {

	private final static int code=15828664;

	public CDataAttrBean create() {
		return new CDataAttrBean();
	}

	public void toField(CDataAttrBean object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("number")) {
			object.setNumber(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("number2")) {
			object.setNumber2(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("page")) {
			object.setPage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(CDataAttrBean object) throws Exception {
		XMLNode node = new XMLNode("cdata-attr");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		Attribute attr_number = new Attribute("number", Object2XML.getInstance().toString(object.getNumber()));
		attrs.add(attr_number);
		Attribute attr_number2 = new Attribute("number2", Object2XML.getInstance().toString(object.getNumber2()));
		attrs.add(attr_number2);
		mapper.addNode(object.getPage(), node, true, "page");
		return node;
	}
}
