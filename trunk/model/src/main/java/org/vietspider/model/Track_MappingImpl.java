package org.vietspider.model;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Track_MappingImpl implements SerializableMapping<Track> {

	private final static int code=14183598;

	public Track create() {
		return new Track();
	}

	public void toField(Track object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("children")) {
			List<Track> list = null;
			list = object.getChildren();
			if(list == null) list = new ArrayList<Track>();
			XML2Object.getInstance().mapCollection(list, Track.class, node);
			object.setChildren(list);
			return;
		}
		if(name.equals("level")) {
			object.setLevel(XML2Object.getInstance().toValue(short.class, value));
			return;
		}

	}

	public XMLNode toNode(Track object) throws Exception {
		XMLNode node = new XMLNode("track");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getChildren(), node, false, "children", "item", "org.vietspider.model.Track");
		mapper.addPrimitiveNode(object.getLevel(), node, false, "level");
		return node;
	}
}
