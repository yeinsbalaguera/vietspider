package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Relation_MappingImpl implements SerializableMapping<Relation> {

	private final static int code=7075153;

	public Relation create() {
		return new Relation();
	}

	public void toField(Relation object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("meta")) {
			object.setMeta(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("relation")) {
			object.setRelation(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("percent")) {
			object.setPercent(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(Relation object) throws Exception {
		XMLNode node = new XMLNode("relation");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getMeta(), node, false, "meta");
		mapper.addNode(object.getRelation(), node, false, "relation");
		mapper.addPrimitiveNode(object.getPercent(), node, false, "percent");
		return node;
	}
}
