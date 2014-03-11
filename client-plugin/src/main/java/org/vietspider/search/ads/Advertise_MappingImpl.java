package org.vietspider.search.ads;


import org.vietspider.search.ads.Advertise;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import java.util.Properties;


public class Advertise_MappingImpl implements SerializableMapping<Advertise> {

	private final static int code=14507272;

	public Advertise create() {
		return new Advertise();
	}

	public void toField(Advertise object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("link")) {
			object.setLink(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("text")) {
			object.setText(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("start")) {
			object.setStart(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("end")) {
			object.setEnd(XML2Object.getInstance().toValue(long.class, value));
			return;
		}
		if(name.equals("properties")) {
			Properties map = null;
			map = object.getProperties();
			if(map == null) map = new Properties();
			XML2Object.getInstance().mapProperties(map, null, null,  node);
			object.setProperties(map);
			return;
		}
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(int.class, value));
			return;
		}

	}

	public XMLNode toNode(Advertise object) throws Exception {
		XMLNode node = new XMLNode("advertise");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getLink(), node, false, "link");
		mapper.addNode(object.getText(), node, false, "text");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addPrimitiveNode(object.getStart(), node, false, "start");
		mapper.addPrimitiveNode(object.getEnd(), node, false, "end");
		mapper.addNode(object.getProperties(), node, false, "properties", "property", null, null);
		mapper.addPrimitiveNode(object.getType(), node, false, "type");
		return node;
	}
}
