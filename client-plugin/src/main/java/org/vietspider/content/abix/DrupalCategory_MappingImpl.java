package org.vietspider.content.abix;


import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.content.abix.DrupalCategoriesConfig.DrupalCategory;


public class DrupalCategory_MappingImpl implements SerializableMapping<DrupalCategory> {

	private final static int code=17960685;

	public DrupalCategory create() {
		return new DrupalCategory();
	}

	public void toField(DrupalCategory object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(DrupalCategory object) throws Exception {
		XMLNode node = new XMLNode("drupal-category");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getName(), node, false, "name");
		return node;
	}
}
