package org.vietspider.db.source.monitor;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SourceInfo_MappingImpl implements SerializableMapping<SourceInfo> {

	private final static int code=31538403;

	public SourceInfo create() {
		return new SourceInfo();
	}

	public void toField(SourceInfo object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("visit")) {
			object.setVisit(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("data")) {
			object.setData(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("link")) {
			object.setLink(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("lastAccess")) {
			object.setLastAccess(XML2Object.getInstance().toValue(long.class, value));
			return;
		}

	}

	public XMLNode toNode(SourceInfo object) throws Exception {
		XMLNode node = new XMLNode("source-info");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addPrimitiveNode(object.getVisit(), node, false, "visit");
		mapper.addPrimitiveNode(object.getData(), node, false, "data");
		mapper.addPrimitiveNode(object.getLink(), node, false, "link");
		mapper.addPrimitiveNode(object.getLastAccess(), node, false, "lastAccess");
		return node;
	}
}
