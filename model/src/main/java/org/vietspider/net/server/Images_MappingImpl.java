package org.vietspider.net.server;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Image;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Images_MappingImpl implements SerializableMapping<Images> {

	private final static int code=32749756;

	public Images create() {
		return new Images();
	}

	public void toField(Images object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("list")) {
			List<Image> list = null;
			list = object.getList();
			if(list == null) list = new ArrayList<Image>();
			XML2Object.getInstance().mapCollection(list, Image.class, node);
			object.setList(list);
			return;
		}

	}

	public XMLNode toNode(Images object) throws Exception {
		XMLNode node = new XMLNode("images");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getList(), node, false, "list", "item", "org.vietspider.bean.Image");
		return node;
	}
}
