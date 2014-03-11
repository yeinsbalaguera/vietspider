package org.vietspider.model.plugin.nukeviet;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class NukeVietSyncData_MappingImpl implements SerializableMapping<NukeVietSyncData> {

	private final static int code=10881285;

	public NukeVietSyncData create() {
		return new NukeVietSyncData();
	}

	public void toField(NukeVietSyncData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("article-id")) {
			object.setArticleId(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("published")) {
			object.setPublished(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("debug")) {
			object.setDebug(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(NukeVietSyncData object) throws Exception {
		XMLNode node = new XMLNode("nukeviet-sync-data");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article-id");
		mapper.addNode(object.getCategoryId(), node, false, "category-id");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isPublished(), node, false, "published");
		mapper.addPrimitiveNode(object.isDebug(), node, false, "debug");
		return node;
	}
}
