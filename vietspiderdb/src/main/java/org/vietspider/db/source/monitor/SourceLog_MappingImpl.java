package org.vietspider.db.source.monitor;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class SourceLog_MappingImpl implements SerializableMapping<SourceLog> {

	private final static int code=16686663;

	public SourceLog create() {
		return new SourceLog();
	}

	public void toField(SourceLog object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("crawl-time")) {
			object.setCrawlTime(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("total-link")) {
			object.setTotalLink(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("total-data")) {
			object.setTotalData(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("last-access")) {
			object.setLastAccess(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("desc")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(SourceLog object) throws Exception {
		XMLNode node = new XMLNode("source-log");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addPrimitiveNode(object.getCrawlTime(), node, false, "crawl-time");
		mapper.addPrimitiveNode(object.getTotalLink(), node, false, "total-link");
		mapper.addPrimitiveNode(object.getTotalData(), node, false, "total-data");
		mapper.addPrimitiveNode(object.getLastAccess(), node, false, "last-access");
		mapper.addNode(object.getDesc(), node, false, "desc");
		return node;
	}
}
