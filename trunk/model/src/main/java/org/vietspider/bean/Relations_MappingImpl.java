package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Relations_MappingImpl implements SerializableMapping<Relations> {

	private final static int code=4872253;

	public Relations create() {
		return new Relations();
	}

	public void toField(Relations object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setMetaId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("items")) {
			List<Relation> list = null;
			list = object.getRelations();
			if(list == null) list = new ArrayList<Relation>();
			XML2Object.getInstance().mapCollection(list, Relation.class, node);
			object.setRelations(list);
			return;
		}

	}

	public XMLNode toNode(Relations object) throws Exception {
		XMLNode node = new XMLNode("relations");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getMetaId(), node, false, "id");
		mapper.addNode(object.getRelations(), node, false, "items", "item", "org.vietspider.bean.Relation");
		return node;
	}
}
