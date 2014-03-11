package org.vietspider.model.plugin.joomla;


import org.vietspider.model.plugin.joomla.XMLJoomlaConfig;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import org.vietspider.model.plugin.joomla.XMLJoomlaConfig.Category;


public class XMLJoomlaConfig_MappingImpl implements SerializableMapping<XMLJoomlaConfig> {

	private final static int code=33263331;

	public XMLJoomlaConfig create() {
		return new XMLJoomlaConfig();
	}

	public void toField(XMLJoomlaConfig object, XMLNode node, String name, String value) throws Exception {
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
		if(name.equals("image-width")) {
			object.setImageWidth(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("published")) {
			object.setPublished(XML2Object.getInstance().toValue(boolean.class, value));
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
		if(name.equals("featured")) {
			object.setFeatured(XML2Object.getInstance().toValue(boolean.class, value));
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
		if(name.equals("image-folder")) {
			object.setImageFolder(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("uri-image-folder")) {
			object.setUriImageFolder(XML2Object.getInstance().toValue(String.class, value));
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

	}

	public XMLNode toNode(XMLJoomlaConfig object) throws Exception {
		XMLNode node = new XMLNode("joomla-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getHomepage(), node, false, "homepage");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addNode(object.getLoginAddress(), node, false, "login-address");
		mapper.addNode(object.getImageWidth(), node, false, "image-width");
		mapper.addPrimitiveNode(object.isPublished(), node, false, "published");
		mapper.addPrimitiveNode(object.isAuto(), node, false, "auto");
		mapper.addPrimitiveNode(object.isUploadImage(), node, false, "upload-image");
		mapper.addPrimitiveNode(object.isFeatured(), node, false, "featured");
		mapper.addPrimitiveNode(object.isAlertMessage(), node, false, "alert-message");
		mapper.addNode(object.getUsername(), node, false, "login-username");
		mapper.addNode(object.getPassword(), node, false, "login-password");
		mapper.addNode(object.getPostAddress(), node, false, "post-address");
		mapper.addNode(object.getImageFolder(), node, false, "image-folder");
		mapper.addNode(object.getUriImageFolder(), node, false, "uri-image-folder");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addNode(object.getTextStyle(), node, false, "text-style");
		mapper.addNode(object.getCategories(), node, false, "categories", "", "org.vietspider.model.plugin.joomla.XMLJoomlaConfig$Category");
		return node;
	}
}
