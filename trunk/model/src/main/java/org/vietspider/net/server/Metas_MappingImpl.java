package org.vietspider.net.server;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Metas_MappingImpl implements SerializableMapping<Metas> {

	private final static int code=29488119;

	public Metas create() {
		return new Metas();
	}

	public void toField(Metas object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("list")) {
			List<Meta> list = null;
			list = object.getList();
			if(list == null) list = new ArrayList<Meta>();
			XML2Object.getInstance().mapCollection(list, Meta.class, node);
			object.setList(list);
			return;
		}

	}

	public XMLNode toNode(Metas object) throws Exception {
		XMLNode node = new XMLNode("Metas");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getList(), node, false, "list", "item", "org.vietspider.bean.Meta");
		return node;
	}
}
