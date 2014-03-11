package org.vietspider.db.database;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class MetaList_MappingImpl implements SerializableMapping<MetaList> {

	private final static int code=31031036;

	public MetaList create() {
		return new MetaList();
	}

	public void toField(MetaList object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("data")) {
			List<Article> list = null;
			list = object.getData();
			if(list == null) list = new ArrayList<Article>();
			XML2Object.getInstance().mapCollection(list, Article.class, node);
			object.setData(list);
			return;
		}
		if(name.equals("total-page")) {
			object.setTotalPage(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("title")) {
			object.setTitle(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("url")) {
			object.setUrl(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("current-page")) {
			object.setCurrentPage(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("action")) {
			object.setAction(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("extension")) {
			object.setExtension(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("page-size")) {
			object.setPageSize(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("total-data")) {
			object.setTotalData(XML2Object.getInstance().toValue(long.class, value));
			return;
		}

	}

	public XMLNode toNode(MetaList object) throws Exception {
		XMLNode node = new XMLNode("meta-list");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getData(), node, false, "data", "item", "org.vietspider.bean.Article");
		mapper.addPrimitiveNode(object.getTotalPage(), node, false, "total-page");
		mapper.addNode(object.getTitle(), node, false, "title");
		mapper.addNode(object.getUrl(), node, false, "url");
		mapper.addPrimitiveNode(object.getCurrentPage(), node, false, "current-page");
		mapper.addNode(object.getAction(), node, false, "action");
		mapper.addNode(object.getExtension(), node, false, "extension");
		mapper.addPrimitiveNode(object.getPageSize(), node, false, "page-size");
		mapper.addPrimitiveNode(object.getTotalData(), node, false, "total-data");
		return node;
	}
}
