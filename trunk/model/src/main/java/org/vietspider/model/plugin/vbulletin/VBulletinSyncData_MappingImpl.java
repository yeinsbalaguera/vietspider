package org.vietspider.model.plugin.vbulletin;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class VBulletinSyncData_MappingImpl implements SerializableMapping<VBulletinSyncData> {

	private final static int code=23136542;

	public VBulletinSyncData create() {
		return new VBulletinSyncData();
	}

	public void toField(VBulletinSyncData object, XMLNode node, String name, String value) throws Exception {
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
		if(name.equals("link-to-source")) {
			object.setLinkToSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("debug")) {
			object.setDebug(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(VBulletinSyncData object) throws Exception {
		XMLNode node = new XMLNode("vbulletin-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article-id");
		mapper.addNode(object.getSectionId(), node, false, "section-id");
		mapper.addNode(object.getCategoryId(), node, false, "category-id");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isDebug(), node, false, "debug");
		return node;
	}
}
