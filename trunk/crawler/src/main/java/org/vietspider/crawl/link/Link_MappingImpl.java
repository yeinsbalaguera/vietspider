package org.vietspider.crawl.link;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.browser.form.Param;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Link_MappingImpl implements SerializableMapping<Link> {

	private final static int code=27060467;

	public Link create() {
		return new Link();
	}

	public void toField(Link object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("address")) {
			object.setAddress(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("url")) {
			object.setUrl(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("normalize-url")) {
			object.setNormalizeURL(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("ref")) {
			object.setRef(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("baseHref")) {
			object.setBaseHref(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("referer")) {
			object.setReferer(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("sessionParam")) {
			object.setSessionParam(XML2Object.getInstance().toValue(String.class, value));
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
		if(name.equals("code")) {
			object.setCode(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("isRss")) {
			object.setRss(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("isData")) {
			object.setIsData(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("isLink")) {
			object.setIsLink(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}

	}

	public XMLNode toNode(Link object) throws Exception {
		XMLNode node = new XMLNode("link");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getAddress(), node, false, "address");
		mapper.addNode(object.getUrl(), node, false, "url");
		mapper.addNode(object.getNormalizeURL(), node, false, "normalize-url");
		mapper.addNode(object.getRef(), node, false, "ref");
		mapper.addNode(object.getBaseHref(), node, false, "baseHref");
		mapper.addNode(object.getReferer(), node, false, "referer");
		mapper.addNode(object.getSessionParam(), node, false, "sessionParam");
		mapper.addNode(object.getParams(), node, false, "params", "param", "org.vietspider.browser.form.Param");
		mapper.addNode(object.getPostContent(), node, false, "post-content");
		mapper.addPrimitiveNode(object.getLevel(), node, false, "level");
		mapper.addPrimitiveNode(object.getCode(), node, false, "code");
		mapper.addPrimitiveNode(object.isRss(), node, false, "isRss");
		mapper.addPrimitiveNode(object.isData(), node, false, "isData");
		mapper.addPrimitiveNode(object.isLink(), node, false, "isLink");
		return node;
	}
}
