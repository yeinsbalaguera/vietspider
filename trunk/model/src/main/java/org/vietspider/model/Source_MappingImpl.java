package org.vietspider.model;


import java.util.List;
import java.util.Properties;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;


public class Source_MappingImpl implements SerializableMapping<Source> {

	private final static int code=33068967;

	public Source create() {
		return new Source();
	}

	public void toField(Source object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("category")) {
			object.setCategory(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("home")) {
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
			object.setHome(values);
			return;
		}
		if(name.equals("update-regions")) {
			object.setUpdateRegion(XML2Object.getInstance().toObject(Region.class, node));
			return;
		}
		if(name.equals("pattern")) {
			object.setPattern(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("encoding")) {
			object.setEncoding(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("depth")) {
			object.setDepth(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("crawl-times")) {
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
			object.setCrawlTimes(values);
			return;
		}
		if(name.equals("priority")) {
			object.setPriority(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("source-type")) {
			object.setGroup(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("extract-type")) {
			object.setExtractType(XML2Object.getInstance().toValue(ExtractType.class, value));
			return;
		}
		if(name.equals("extract-regions")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Region[] values = new Region[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(Region.class, n);
			}
			object.setExtractRegion(values);
			return;
		}
		if(name.equals("process-regions")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Region[] values = new Region[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(Region.class, n);
			}
			object.setProcessRegion(values);
			return;
		}
		if(name.equals("properties")) {
			Properties map = null;
			map = object.getProperties();
			if(map == null) map = new Properties();
			XML2Object.getInstance().mapProperties(map, null, null,  node);
			object.setProperties(map);
			return;
		}
		if(name.equals("lastModified")) {
			object.setLastModified(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("lastCrawledTime")) {
			object.setLastCrawledTime(XML2Object.getInstance().toValue(long.class, value));
			return;
		}

	}

	public XMLNode toNode(Source object) throws Exception {
		XMLNode node = new XMLNode("source");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getCategory(), node, false, "category");
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getHome(), node, false, "home", "url", "java.lang.String");
		mapper.addNode(object.getUpdateRegion(), node, false, "update-regions");
		mapper.addNode(object.getPattern(), node, false, "pattern");
		Attribute attr_encoding = new Attribute("encoding", Object2XML.getInstance().toString(object.getEncoding()));
		attrs.add(attr_encoding);
		Attribute attr_depth = new Attribute("depth", Object2XML.getInstance().toString(object.getDepth()));
		attrs.add(attr_depth);
		mapper.addNode(object.getCrawlTimes(), node, false, "crawl-times", "time", "java.lang.String");
		Attribute attr_priority = new Attribute("priority", Object2XML.getInstance().toString(object.getPriority()));
		attrs.add(attr_priority);
		Attribute attr_group = new Attribute("source-type", Object2XML.getInstance().toString(object.getGroup()));
		attrs.add(attr_group);
		Attribute attr_extractType = new Attribute("extract-type", Object2XML.getInstance().toString(object.getExtractType()));
		attrs.add(attr_extractType);
		mapper.addNode(object.getExtractRegion(), node, false, "extract-regions", "", "org.vietspider.model.Region");
		mapper.addNode(object.getProcessRegion(), node, false, "process-regions", "", "org.vietspider.model.Region");
		mapper.addNode(object.getProperties(), node, false, "properties", "property", null, null);
		Attribute attr_lastModified = new Attribute("lastModified", Object2XML.getInstance().toString(object.getLastModified()));
		attrs.add(attr_lastModified);
		Attribute attr_lastCrawledTime = new Attribute("lastCrawledTime", Object2XML.getInstance().toString(object.getLastCrawledTime()));
		attrs.add(attr_lastCrawledTime);
		return node;
	}
}
