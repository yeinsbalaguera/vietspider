package org.vietspider.crawl.plugin.handler;


import java.util.List;

import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.ArticleCleaner;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.RemoveAttribute;
import org.vietspider.html.Name;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class ArticleCleaner_MappingImpl implements SerializableMapping<ArticleCleaner> {

	private final static int code=4660784;

	public ArticleCleaner create() {
		return new ArticleCleaner();
	}

	public void toField(ArticleCleaner object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("remove-attribute")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			RemoveAttribute[] values = new RemoveAttribute[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(RemoveAttribute.class, n);
			}
			object.setRemoveAttribute(values);
			return;
		}
		if(name.equals("comment-tag")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Name[] values = new Name[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(Name.class, n.getChild(0).getTextValue());
			}
			object.setCommentTag(values);
			return;
		}
		if(name.equals("remove-node")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Name[] values = new Name[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(Name.class, n.getChild(0).getTextValue());
			}
			object.setRemoveNode(values);
			return;
		}
		if(name.equals("remove-text")) {
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
			object.setRemoveText(values);
			return;
		}

	}

	public XMLNode toNode(ArticleCleaner object) throws Exception {
		XMLNode node = new XMLNode("article-cleaner");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getRemoveAttribute(), node, false, "remove-attribute", "", "org.vietspider.crawl.plugin.handler.DocumentFormatCleaner$RemoveAttribute");
		mapper.addNode(object.getCommentTag(), node, false, "comment-tag", "tag", "org.vietspider.html.Name");
		mapper.addNode(object.getRemoveNode(), node, false, "remove-node", "node", "org.vietspider.html.Name");
		mapper.addNode(object.getRemoveText(), node, false, "remove-text", "text", "java.lang.String");
		return node;
	}
}
