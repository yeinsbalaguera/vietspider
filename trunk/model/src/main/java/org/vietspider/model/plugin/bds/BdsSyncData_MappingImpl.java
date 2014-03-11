package org.vietspider.model.plugin.bds;


import org.vietspider.model.plugin.bds.BdsSyncData;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;


public class BdsSyncData_MappingImpl implements SerializableMapping<BdsSyncData> {

	private final static int code=1641745;

	public BdsSyncData create() {
		return new BdsSyncData();
	}

	public void toField(BdsSyncData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("article-id")) {
			object.setArticleId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("region-id")) {
			object.setRegionId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("category-id")) {
			object.setCategoryId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("debug")) {
			object.setDebug(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(BdsSyncData object) throws Exception {
		XMLNode node = new XMLNode("bds-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article-id");
		mapper.addNode(object.getRegionId(), node, false, "region-id");
		mapper.addNode(object.getCategoryId(), node, false, "category-id");
		mapper.addPrimitiveNode(object.isDebug(), node, false, "debug");
		return node;
	}
}
