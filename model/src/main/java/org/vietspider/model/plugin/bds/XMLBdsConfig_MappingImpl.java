package org.vietspider.model.plugin.bds;


import org.vietspider.model.plugin.bds.XMLBdsConfig;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Category;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;


public class XMLBdsConfig_MappingImpl implements SerializableMapping<XMLBdsConfig> {

	private final static int code=138093;

	public XMLBdsConfig create() {
		return new XMLBdsConfig();
	}

	public void toField(XMLBdsConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("homepage")) {
			object.setHomepage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("charset")) {
			object.setCharset(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("auto")) {
			object.setAuto(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("upload-image")) {
			object.setUploadImage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("alert-message")) {
			object.setAlertMessage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("login-address")) {
			object.setLoginAddress(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("categories")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Category[] values = new Category[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(Category.class, n);
			}
			object.setCategories(values);
			return;
		}
		if(name.equals("regions")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			Region[] values = new Region[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toObject(Region.class, n);
			}
			object.setRegions(values);
			return;
		}

	}

	public XMLNode toNode(XMLBdsConfig object) throws Exception {
		XMLNode node = new XMLNode("bds-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getHomepage(), node, false, "homepage");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addPrimitiveNode(object.isAuto(), node, false, "auto");
		mapper.addPrimitiveNode(object.isUploadImage(), node, false, "upload-image");
		mapper.addPrimitiveNode(object.isAlertMessage(), node, false, "alert-message");
		mapper.addNode(object.getLoginAddress(), node, false, "login-address");
		mapper.addNode(object.getUsername(), node, false, "login-username");
		mapper.addNode(object.getPassword(), node, false, "login-password");
		mapper.addNode(object.getCategories(), node, false, "categories", "", "org.vietspider.model.plugin.bds.XMLBdsConfig$Category");
		mapper.addNode(object.getRegions(), node, false, "regions", "", "org.vietspider.model.plugin.bds.XMLBdsConfig$Region");
		return node;
	}
}
