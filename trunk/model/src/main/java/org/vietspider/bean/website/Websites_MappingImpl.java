package org.vietspider.bean.website;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Websites_MappingImpl implements SerializableMapping<Websites> {

	private final static int code=26519787;

	public Websites create() {
		return new Websites();
	}

	public void toField(Websites object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("list")) {
			List<Website> list = null;
			list = object.getList();
			if(list == null) list = new ArrayList<Website>();
			XML2Object.getInstance().mapCollection(list, Website.class, node);
			object.setList(list);
			return;
		}
		if(name.equals("page")) {
			object.setPage(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("page-size")) {
			object.setPageSize(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("totalPage")) {
			object.setTotalPage(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("date")) {
			object.setDate(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("ip")) {
			object.setIp(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("status")) {
			object.setStatus(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("language")) {
			object.setLanguage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Websites object) throws Exception {
		XMLNode node = new XMLNode("websites");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getList(), node, false, "list", "item", "org.vietspider.bean.website.Website");
		mapper.addPrimitiveNode(object.getPage(), node, false, "page");
		mapper.addPrimitiveNode(object.getPageSize(), node, false, "page-size");
		mapper.addPrimitiveNode(object.getTotalPage(), node, false, "totalPage");
		mapper.addNode(object.getDate(), node, false, "date");
		mapper.addNode(object.getIp(), node, false, "ip");
		mapper.addPrimitiveNode(object.getStatus(), node, false, "status");
		mapper.addNode(object.getLanguage(), node, false, "language");
		return node;
	}
}
