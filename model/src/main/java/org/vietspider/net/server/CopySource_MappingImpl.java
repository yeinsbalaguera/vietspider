package org.vietspider.net.server;


import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class CopySource_MappingImpl implements SerializableMapping<CopySource> {

	private final static int code=7813825;

	public CopySource create() {
		return new CopySource();
	}

	public void toField(CopySource object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("src_group")) {
			object.setSrcGroup(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("des_group")) {
			object.setDesGroup(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("src_category")) {
			object.setSrcCategory(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("is_cut")) {
			object.setDelete(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("src_names")) {
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
			object.setSrcNames(values);
			return;
		}
		if(name.equals("des_categories")) {
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
			object.setDesCategories(values);
			return;
		}

	}

	public XMLNode toNode(CopySource object) throws Exception {
		XMLNode node = new XMLNode("copy_sources");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getSrcGroup(), node, false, "src_group");
		mapper.addNode(object.getDesGroup(), node, false, "des_group");
		mapper.addNode(object.getSrcCategory(), node, false, "src_category");
		mapper.addPrimitiveNode(object.isDelete(), node, false, "is_cut");
		mapper.addNode(object.getSrcNames(), node, false, "src_names", "name", "java.lang.String");
		mapper.addNode(object.getDesCategories(), node, false, "des_categories", "category", "java.lang.String");
		return node;
	}
}
