package org.vietspider.model.plugin.joomla;


import org.vietspider.model.plugin.joomla.JoomlaSyncData;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;


public class JoomlaSyncData_MappingImpl implements SerializableMapping<JoomlaSyncData> {

	private final static int code=5268497;

	public JoomlaSyncData create() {
		return new JoomlaSyncData();
	}

	public void toField(JoomlaSyncData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("article-id")) {
			object.setArticleId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("section-id")) {
			object.setSectionId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("category-id")) {
			object.setCategoryId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("meta-image-width")) {
			object.setMetaImageWidth(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("link-to-source")) {
			object.setLinkToSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("frontpage")) {
			object.setFeatured(XML2Object.getInstance().toValue(boolean.class, value));
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

	public XMLNode toNode(JoomlaSyncData object) throws Exception {
		XMLNode node = new XMLNode("joomla-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article-id");
		mapper.addNode(object.getSectionId(), node, false, "section-id");
		mapper.addNode(object.getCategoryId(), node, false, "category-id");
		mapper.addPrimitiveNode(object.getMetaImageWidth(), node, false, "meta-image-width");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isFeatured(), node, false, "frontpage");
		mapper.addPrimitiveNode(object.isPublished(), node, false, "published");
		mapper.addPrimitiveNode(object.isDebug(), node, false, "debug");
		return node;
	}
}
