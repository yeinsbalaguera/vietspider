package org.vietspider.net.server;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class CrawlerStatus_MappingImpl implements SerializableMapping<CrawlerStatus> {

	private final static int code=3243465;

	public CrawlerStatus create() {
		return new CrawlerStatus();
	}

	public void toField(CrawlerStatus object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("status")) {
			object.setStatus(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("sources")) {
			List<String> list = null;
			list = object.getSources();
			if(list == null) list = new ArrayList<String>();
			XML2Object.getInstance().mapCollection(list, String.class, node);
			object.setSources(list);
			return;
		}
		if(name.equals("threadStatus")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			String[] values = new String[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(String.class, n.getChild(0).getTextValue());
			}
			object.setThreadStatus(values);
			return;
		}
		if(name.equals("totalThread")) {
			object.setTotalThread(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(CrawlerStatus object) throws Exception {
		XMLNode node = new XMLNode("crawler-status");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getStatus(), node, false, "status");
		mapper.addNode(object.getSources(), node, false, "sources", "item", "java.lang.String");
		mapper.addNode(object.getThreadStatus(), node, false, "threadStatus", "item", "java.lang.String");
		mapper.addPrimitiveNode(object.getTotalThread(), node, false, "totalThread");
		return node;
	}
}
