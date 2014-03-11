package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Article_MappingImpl implements SerializableMapping<Article> {

	private final static int code=25975631;

	public Article create() {
		return new Article();
	}

	public void toField(Article object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("id")) {
			object.setId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("meta")) {
			object.setMeta(XML2Object.getInstance().toObject(Meta.class, node));
			return;
		}
		if(name.equals("content")) {
			object.setContent(XML2Object.getInstance().toObject(Content.class, node));
			return;
		}
		if(name.equals("domain")) {
			object.setDomain(XML2Object.getInstance().toObject(Domain.class, node));
			return;
		}
		if(name.equals("meta-relations")) {
			List<MetaRelation> list = null;
			list = object.getMetaRelations();
			if(list == null) list = new ArrayList<MetaRelation>();
			XML2Object.getInstance().mapCollection(list, MetaRelation.class, node);
			object.setMetaRelations(list);
			return;
		}
		if(name.equals("relations")) {
			List<Relation> list = null;
			list = object.getRelations();
			if(list == null) list = new ArrayList<Relation>();
			XML2Object.getInstance().mapCollection(list, Relation.class, node);
			object.setRelations(list);
			return;
		}
		if(name.equals("images")) {
			List<Image> list = null;
			list = object.getImages();
			if(list == null) list = new ArrayList<Image>();
			XML2Object.getInstance().mapCollection(list, Image.class, node);
			object.setImages(list);
			return;
		}
		if(name.equals("nld-record")) {
			object.setNlpRecord(XML2Object.getInstance().toObject(NLPRecord.class, node));
			return;
		}
		if(name.equals("status")) {
			object.setStatus(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("save-type")) {
			object.setSaveType(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("score")) {
			object.setScore(XML2Object.getInstance().toValue(float.class, value));
			return;
		}

	}

	public XMLNode toNode(Article object) throws Exception {
		XMLNode node = new XMLNode("article");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getId(), node, false, "id");
		mapper.addNode(object.getMeta(), node, false, "meta");
		mapper.addNode(object.getContent(), node, false, "content");
		mapper.addNode(object.getDomain(), node, false, "domain");
		mapper.addNode(object.getMetaRelations(), node, false, "meta-relations", "item", "org.vietspider.bean.MetaRelation");
		mapper.addNode(object.getRelations(), node, false, "relations", "item", "org.vietspider.bean.Relation");
		mapper.addNode(object.getImages(), node, false, "images", "item", "org.vietspider.bean.Image");
		mapper.addNode(object.getNlpRecord(), node, false, "nld-record");
		mapper.addPrimitiveNode(object.getStatus(), node, false, "status");
		mapper.addPrimitiveNode(object.getSaveType(), node, false, "save-type");
		mapper.addPrimitiveNode(object.getScore(), node, false, "score");
		return node;
	}
}
