package org.vietspider.parser.rss2;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class EntryItem_MappingImpl implements SerializableMapping<EntryItem> {

	private final static int code=31048679;

	public EntryItem create() {
		return new EntryItem();
	}

	public void toField(EntryItem object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("summary")) {
			object.setDesc(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("content")) {
			object.setContent(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("image")) {
			object.setImage(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("published")) {
			object.setTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(EntryItem object) throws Exception {
		XMLNode node = new XMLNode("entry");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getDesc(), node, false, "summary");
		mapper.addNode(object.getContent(), node, false, "content");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addNode(object.getTime(), node, false, "published");
		return node;
	}
}
