package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class MetaRelations_MappingImpl implements SerializableMapping<MetaRelations> {

	private final static int code=23399658;

	public MetaRelations create() {
		return new MetaRelations();
	}

	public void toField(MetaRelations object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setMetaId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("items")) {
			List<MetaRelation> list = null;
			list = object.getMetaRelations();
			if(list == null) list = new ArrayList<MetaRelation>();
			XML2Object.getInstance().mapCollection(list, MetaRelation.class, node);
			object.setMetaRelations(list);
			return;
		}

	}

	public XMLNode toNode(MetaRelations object) throws Exception {
		XMLNode node = new XMLNode("meta-relations");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getMetaId(), node, false, "id");
		mapper.addNode(object.getMetaRelations(), node, false, "items", "item", "org.vietspider.bean.MetaRelation");
		return node;
	}
}
