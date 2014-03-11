package org.vietspider.content.abix;


import org.vietspider.content.abix.DrupalCategoriesConfig;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import org.vietspider.content.abix.DrupalCategoriesConfig.DrupalCategory;


public class DrupalCategoriesConfig_MappingImpl implements SerializableMapping<DrupalCategoriesConfig> {

	private final static int code=27658865;

	public DrupalCategoriesConfig create() {
		return new DrupalCategoriesConfig();
	}

	public void toField(DrupalCategoriesConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("categories")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			DrupalCategory[] values = new DrupalCategory[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(DrupalCategory.class, n);
			}
			object.setCategories(values);
			return;
		}

	}

	public XMLNode toNode(DrupalCategoriesConfig object) throws Exception {
		XMLNode node = new XMLNode("drupal-categories");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getCategories(), node, false, "categories", "item", "org.vietspider.content.abix.DrupalCategoriesConfig$DrupalCategory");
		return node;
	}
}
