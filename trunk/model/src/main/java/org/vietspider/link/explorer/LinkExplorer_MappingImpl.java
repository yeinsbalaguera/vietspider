package org.vietspider.link.explorer;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.browser.form.Param;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class LinkExplorer_MappingImpl implements SerializableMapping<LinkExplorer> {

	private final static int code=8490467;

	public LinkExplorer create() {
		return new LinkExplorer();
	}

	public void toField(LinkExplorer object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("address")) {
			object.setAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("url")) {
			object.setUrl(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("ref")) {
			object.setRef(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("base-url")) {
			object.setBaseHref(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("referer")) {
			object.setReferer(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("params")) {
			List<Param> list = null;
			list = object.getParams();
			if(list == null) list = new ArrayList<Param>();
			XML2Object.getInstance().mapCollection(list, Param.class, node);
			object.setParams(list);
			return;
		}
		if(name.equals("post-content")) {
			object.setPostContent(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("level")) {
			object.setLevel(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("is-rss")) {
			object.setRss(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("is-data")) {
			object.setIsData(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("is-link")) {
			object.setIsLink(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(LinkExplorer object) throws Exception {
		XMLNode node = new XMLNode("link-explorer");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getAddress(), node, false, "address");
		mapper.addNode(object.getUrl(), node, false, "url");
		mapper.addNode(object.getRef(), node, false, "ref");
		mapper.addNode(object.getBaseHref(), node, false, "base-url");
		mapper.addNode(object.getReferer(), node, false, "referer");
		mapper.addNode(object.getParams(), node, false, "params", "param", "org.vietspider.browser.form.Param");
		mapper.addNode(object.getPostContent(), node, false, "post-content");
		mapper.addPrimitiveNode(object.getLevel(), node, false, "level");
		mapper.addPrimitiveNode(object.isRss(), node, false, "is-rss");
		mapper.addPrimitiveNode(object.isData(), node, false, "is-data");
		mapper.addPrimitiveNode(object.isLink(), node, false, "is-link");
		return node;
	}
}
