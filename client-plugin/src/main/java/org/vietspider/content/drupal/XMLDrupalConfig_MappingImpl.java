package org.vietspider.content.drupal;


import org.vietspider.content.drupal.XMLDrupalConfig;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import org.vietspider.content.drupal.XMLDrupalConfig.Category;


public class XMLDrupalConfig_MappingImpl implements SerializableMapping<XMLDrupalConfig> {

	private final static int code=25540387;

	public XMLDrupalConfig create() {
		return new XMLDrupalConfig();
	}

	public void toField(XMLDrupalConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("homepage")) {
			object.setHomepage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("login")) {
			object.setLoginAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("charset")) {
			object.setCharset(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("image-upload-address")) {
			object.setImageUploadAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("meta-image-width")) {
			object.setMetaImageWidth(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("link-to-source")) {
			object.setLinkToSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("alert-message")) {
			object.setAlertMessage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("auto-sync")) {
			object.setAutoSync(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(XMLDrupalConfig object) throws Exception {
		XMLNode node = new XMLNode("drupal-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getHomepage(), node, false, "homepage");
		mapper.addNode(object.getLoginAddress(), node, false, "login");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addNode(object.getUsername(), node, false, "login-username");
		mapper.addNode(object.getPassword(), node, false, "login-password");
		mapper.addNode(object.getPostAddress(), node, false, "post-address");
		mapper.addNode(object.getImageUploadAddress(), node, false, "image-upload-address");
		mapper.addNode(object.getMetaImageWidth(), node, false, "meta-image-width");
		mapper.addNode(object.getCategories(), node, false, "categories", "", "org.vietspider.content.drupal.XMLDrupalConfig$Category");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isAlertMessage(), node, false, "alert-message");
		mapper.addPrimitiveNode(object.isAutoSync(), node, false, "auto-sync");
		return node;
	}
}
