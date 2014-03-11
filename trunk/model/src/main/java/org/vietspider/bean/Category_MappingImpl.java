package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.TreeMenu2.Category;
import org.vietspider.bean.TreeMenu2.SourceName;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Category_MappingImpl implements SerializableMapping<Category> {

	private final static int code=12970890;

	public Category create() {
		return new Category();
	}

	public void toField(Category object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("category")) {
			object.setCategory(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("sources_name")) {
			List<SourceName> list = null;
			list = object.getSourcesName();
			if(list == null) list = new ArrayList<SourceName>();
			XML2Object.getInstance().mapCollection(list, SourceName.class, node);
			object.setSources(list);
			return;
		}
		if(name.equals("total")) {
			object.setTotal(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(Category object) throws Exception {
		XMLNode node = new XMLNode("category");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getCategory(), node, false, "category");
		mapper.addNode(object.getSourcesName(), node, false, "sources_name", "item", "org.vietspider.bean.TreeMenu2$SourceName");
		mapper.addPrimitiveNode(object.getTotal(), node, false, "total");
		return node;
	}
}
