package org.vietspider.bean;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class MetaRelation_MappingImpl implements SerializableMapping<MetaRelation> {

	private final static int code=13646336;

	public MetaRelation create() {
		return new MetaRelation();
	}

	public void toField(MetaRelation object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("desc")) {
			object.setDes(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("percent")) {
			object.setPercent(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("source")) {
			object.setSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("date")) {
			object.setDate(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(MetaRelation object) throws Exception {
		XMLNode node = new XMLNode("meta_relation");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getDes(), node, false, "desc");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addPrimitiveNode(object.getPercent(), node, false, "percent");
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getSource(), node, false, "source");
		mapper.addNode(object.getDate(), node, false, "date");
		mapper.addNode(object.getTime(), node, false, "time");
		return node;
	}
}
