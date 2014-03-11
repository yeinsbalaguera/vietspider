package org.vietspider.bean.website;


import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Website_MappingImpl implements SerializableMapping<Website> {

	private final static int code=22640871;

	public Website create() {
		return new Website();
	}

	public void toField(Website object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("address")) {
			object.setAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("host")) {
			object.setHost(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("desc")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("language")) {
			object.setLanguage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("date")) {
			object.setDate(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("status")) {
			object.setStatus(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("ip")) {
			object.setIp(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("config")) {
			object.setConfig(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("source")) {
			object.setSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("charset")) {
			object.setCharset(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("homepages")) {
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
			object.setHomepages(values);
			return;
		}
		if(name.equals("timedownload")) {
			object.setTimeDownload(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("path")) {
			object.setPath(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("hash")) {
			object.setHash(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("html")) {
			object.setHtml(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Website object) throws Exception {
		XMLNode node = new XMLNode("website");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getAddress(), node, false, "address");
		mapper.addNode(object.getHost(), node, false, "host");
		mapper.addNode(object.getDesc(), node, false, "desc");
		mapper.addNode(object.getLanguage(), node, false, "language");
		mapper.addNode(object.getDate(), node, false, "date");
		mapper.addPrimitiveNode(object.getStatus(), node, false, "status");
		mapper.addNode(object.getIp(), node, false, "ip");
		mapper.addNode(object.getConfig(), node, false, "config");
		mapper.addNode(object.getSource(), node, false, "source");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addNode(object.getHomepages(), node, false, "homepages", "url", "java.lang.String");
		mapper.addPrimitiveNode(object.getTimeDownload(), node, false, "timedownload");
		mapper.addNode(object.getPath(), node, false, "path");
		mapper.addNode(object.getHash(), node, false, "hash");
		mapper.addNode(object.getHtml(), node, true, "html");
		return node;
	}
}
