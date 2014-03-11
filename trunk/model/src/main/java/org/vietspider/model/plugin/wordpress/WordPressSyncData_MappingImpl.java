package org.vietspider.model.plugin.wordpress;


import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class WordPressSyncData_MappingImpl implements SerializableMapping<WordPressSyncData> {

	private final static int code=18297884;

	public WordPressSyncData create() {
		return new WordPressSyncData();
	}

	public void toField(WordPressSyncData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("article-id")) {
			object.setArticleId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("category-ids")) {
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
			object.setCategoryIds(values);
			return;
		}
		if(name.equals("link-to-source")) {
			object.setLinkToSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("published")) {
			object.setPublished(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("debug")) {
			object.setDebug(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(WordPressSyncData object) throws Exception {
		XMLNode node = new XMLNode("wordpress-sync-data");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article-id");
		mapper.addNode(object.getCategoryIds(), node, false, "category-ids", "id", "java.lang.String");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isPublished(), node, false, "published");
		mapper.addPrimitiveNode(object.isDebug(), node, false, "debug");
		return node;
	}
}
