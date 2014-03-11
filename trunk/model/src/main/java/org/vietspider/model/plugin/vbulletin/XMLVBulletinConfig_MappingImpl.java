package org.vietspider.model.plugin.vbulletin;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.model.plugin.Category;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class XMLVBulletinConfig_MappingImpl implements SerializableMapping<XMLVBulletinConfig> {

	private final static int code=28335152;

	public XMLVBulletinConfig create() {
		return new XMLVBulletinConfig();
	}

	public void toField(XMLVBulletinConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("homepage")) {
			object.setHomepage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("charset")) {
			object.setCharset(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("login-address")) {
			object.setLoginAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("auto")) {
			object.setAuto(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("alert-message")) {
			object.setAlertMessage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("login-username")) {
			object.setUsername(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("login-password")) {
			object.setPassword(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("post-address")) {
			object.setPostAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("link-to-source")) {
			object.setLinkToSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("text-style")) {
			object.setTextStyle(XML2Object.getInstance().toValue(String.class, value));
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

	}

	public XMLNode toNode(XMLVBulletinConfig object) throws Exception {
		XMLNode node = new XMLNode("vbulletin-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getHomepage(), node, false, "homepage");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addNode(object.getLoginAddress(), node, false, "login-address");
		mapper.addPrimitiveNode(object.isAuto(), node, false, "auto");
		mapper.addPrimitiveNode(object.isAlertMessage(), node, false, "alert-message");
		mapper.addNode(object.getUsername(), node, false, "login-username");
		mapper.addNode(object.getPassword(), node, false, "login-password");
		mapper.addNode(object.getPostAddress(), node, false, "post-address");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addNode(object.getTextStyle(), node, false, "text-style");
		mapper.addNode(object.getCategories(), node, false, "categories", "", "org.vietspider.model.plugin.Category");
		return node;
	}
}
