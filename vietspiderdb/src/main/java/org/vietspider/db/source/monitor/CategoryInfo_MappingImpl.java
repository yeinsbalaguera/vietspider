package org.vietspider.db.source.monitor;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class CategoryInfo_MappingImpl implements SerializableMapping<CategoryInfo> {

	private final static int code=30632266;

	public CategoryInfo create() {
		return new CategoryInfo();
	}

	public void toField(CategoryInfo object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("category")) {
			object.setCategory(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("sources")) {
			List<SourceInfo> list = null;
			list = object.getSources();
			if(list == null) list = new ArrayList<SourceInfo>();
			XML2Object.getInstance().mapCollection(list, SourceInfo.class, node);
			object.setSources(list);
			return;
		}

	}

	public XMLNode toNode(CategoryInfo object) throws Exception {
		XMLNode node = new XMLNode("category-info");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getCategory(), node, false, "category");
		mapper.addNode(object.getSources(), node, false, "sources", "item", "org.vietspider.db.source.monitor.SourceInfo");
		return node;
	}
}
