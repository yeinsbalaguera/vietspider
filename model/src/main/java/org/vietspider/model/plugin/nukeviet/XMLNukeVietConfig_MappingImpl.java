package org.vietspider.model.plugin.nukeviet;


import java.util.List;

import org.vietspider.model.plugin.nukeviet.XMLNukeVietConfig.Category;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class XMLNukeVietConfig_MappingImpl implements SerializableMapping<XMLNukeVietConfig> {

	private final static int code=18342976;

	public XMLNukeVietConfig create() {
		return new XMLNukeVietConfig();
	}

	public void toField(XMLNukeVietConfig object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("homepage")) {
			object.setHomepage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("login")) {
			object.setLoginAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("upload-image")) {
			object.setUploadImage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("published")) {
			object.setPublished(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("charset")) {
			object.setCharset(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("boundary")) {
			object.setBoundary(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("image-position")) {
			object.setImagePosition(XML2Object.getInstance().toValue(String.class, value));
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

	public XMLNode toNode(XMLNukeVietConfig object) throws Exception {
		XMLNode node = new XMLNode("nukeviet-config");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getHomepage(), node, false, "homepage");
		mapper.addNode(object.getLoginAddress(), node, false, "login");
		mapper.addPrimitiveNode(object.isUploadImage(), node, false, "upload-image");
		mapper.addPrimitiveNode(object.isPublished(), node, false, "published");
		mapper.addNode(object.getCharset(), node, false, "charset");
		mapper.addNode(object.getBoundary(), node, false, "boundary");
		mapper.addNode(object.getUsername(), node, false, "login-username");
		mapper.addNode(object.getPassword(), node, false, "login-password");
		mapper.addNode(object.getPostAddress(), node, false, "post-address");
		mapper.addNode(object.getImageUploadAddress(), node, false, "image-upload-address");
		mapper.addNode(object.getImagePosition(), node, false, "image-position");
		mapper.addNode(object.getMetaImageWidth(), node, false, "meta-image-width");
		mapper.addNode(object.getCategories(), node, false, "categories", "", "org.vietspider.model.plugin.nukeviet.XMLNukeVietConfig$Category");
		mapper.addNode(object.getLinkToSource(), node, false, "link-to-source");
		mapper.addPrimitiveNode(object.isAlertMessage(), node, false, "alert-message");
		mapper.addPrimitiveNode(object.isAutoSync(), node, false, "auto-sync");
		return node;
	}
}
