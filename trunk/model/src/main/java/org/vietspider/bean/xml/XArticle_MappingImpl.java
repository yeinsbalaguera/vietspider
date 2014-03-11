package org.vietspider.bean.xml;


import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class XArticle_MappingImpl implements SerializableMapping<XArticle> {

	private final static int code=33537384;

	public XArticle create() {
		return new XArticle();
	}

	public void toField(XArticle object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("desc")) {
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
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("source_time")) {
			object.setSourceTime(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("domain")) {
			object.setDomain(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("url")) {
			object.setSource(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(XArticle object) throws Exception {
		XMLNode node = new XMLNode("article");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getDesc(), node, false, "desc");
		mapper.addNode(object.getContent(), node, false, "content");
		mapper.addNode(object.getImage(), node, false, "image");
		mapper.addNode(object.getTime(), node, false, "time");
		mapper.addNode(object.getSourceTime(), node, false, "source_time");
		mapper.addNode(object.getDomain(), node, false, "domain");
		mapper.addNode(object.getSource(), node, false, "url");
		return node;
	}
}
