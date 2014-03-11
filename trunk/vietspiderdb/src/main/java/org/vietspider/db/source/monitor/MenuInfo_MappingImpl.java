package org.vietspider.db.source.monitor;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class MenuInfo_MappingImpl implements SerializableMapping<MenuInfo> {

	private final static int code=15084089;

	public MenuInfo create() {
		return new MenuInfo();
	}

	public void toField(MenuInfo object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("categories")) {
			List<CategoryInfo> list = null;
			list = object.getCategories();
			if(list == null) list = new ArrayList<CategoryInfo>();
			XML2Object.getInstance().mapCollection(list, CategoryInfo.class, node);
			object.setCategories(list);
			return;
		}

	}

	public XMLNode toNode(MenuInfo object) throws Exception {
		XMLNode node = new XMLNode("menu-info");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getCategories(), node, false, "categories", "item", "org.vietspider.db.source.monitor.CategoryInfo");
		return node;
	}
}
