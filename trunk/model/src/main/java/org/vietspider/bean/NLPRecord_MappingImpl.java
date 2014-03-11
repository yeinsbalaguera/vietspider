package org.vietspider.bean;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class NLPRecord_MappingImpl implements SerializableMapping<NLPRecord> {

	private final static int code=2944870;

	public NLPRecord create() {
		return new NLPRecord();
	}

	public void toField(NLPRecord object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("meta-id")) {
			object.setMetaId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("records")) {
			List<NLPRecordItem> list = null;
			list = object.getItems();
			if(list == null) list = new ArrayList<NLPRecordItem>();
			XML2Object.getInstance().mapCollection(list, NLPRecordItem.class, node);
			object.setValues(list);
			return;
		}
		if(name.equals("datas")) {
			List<NLPData> list = null;
			list = object.getDatas();
			if(list == null) list = new ArrayList<NLPData>();
			XML2Object.getInstance().mapCollection(list, NLPData.class, node);
			object.setDatas(list);
			return;
		}

	}

	public XMLNode toNode(NLPRecord object) throws Exception {
		XMLNode node = new XMLNode("nlp-record");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getMetaId(), node, false, "meta-id");
		mapper.addNode(object.getItems(), node, false, "records", "item", "org.vietspider.bean.NLPRecordItem");
		mapper.addNode(object.getDatas(), node, false, "datas", "item", "org.vietspider.bean.NLPData");
		return node;
	}
}
