package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.TreeMenu2.Category;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class TreeMenu2_MappingImpl implements SerializableMapping<TreeMenu2> {

	private final static int code=164819;

	public TreeMenu2 create() {
		return new TreeMenu2();
	}

	public void toField(TreeMenu2 object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("date")) {
			object.setDate(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("categories")) {
			List<Category> list = null;
			list = object.getCategories();
			if(list == null) list = new ArrayList<Category>();
			XML2Object.getInstance().mapCollection(list, Category.class, node);
			object.setCategories(list);
			return;
		}
		if(name.equals("total")) {
			object.setTotal(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(TreeMenu2 object) throws Exception {
		XMLNode node = new XMLNode("tree_menu");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getDate(), node, false, "date");
		mapper.addNode(object.getCategories(), node, false, "categories", "item", "org.vietspider.bean.TreeMenu2$Category");
		mapper.addPrimitiveNode(object.getTotal(), node, false, "total");
		return node;
	}
}
