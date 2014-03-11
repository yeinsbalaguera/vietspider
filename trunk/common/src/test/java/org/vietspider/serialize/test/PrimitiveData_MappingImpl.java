package org.vietspider.serialize.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class PrimitiveData_MappingImpl implements SerializableMapping<PrimitiveData> {

	private final static int code=14802865;

	public PrimitiveData create() {
		return new PrimitiveData();
	}

	public void toField(PrimitiveData object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("age")) {
			object.setAge(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("time")) {
			object.setTime(XML2Object.getInstance().toValue(Long.class, value));
			return;
		}
		if(name.equals("prices")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			double[] values = new double[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(double.class, n.getChild(0).getTextValue());
			}
			object.setPrices(values);
			return;
		}
		if(name.equals("create")) {
			object.setCreate(XML2Object.getInstance().toValue(Date.class, value));
			return;
		}
		if(name.equals("character")) {
			object.setCharacter(XML2Object.getInstance().toValue(char.class, value));
			return;
		}
		if(name.equals("trust")) {
			object.setTrust(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("pbeans")) {
			List<CDataAttrBean> list = null;
			list = object.getBeans();
			if(list == null) list = new ArrayList<CDataAttrBean>();
			XML2Object.getInstance().mapCollection(list, CDataAttrBean.class, node);
			object.setBeans(list);
			return;
		}

	}

	public XMLNode toNode(PrimitiveData object) throws Exception {
		XMLNode node = new XMLNode("primitive-data");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getAge(), node, false, "age");
		mapper.addNode(object.getTime(), node, false, "time");
		mapper.addNode(object.getPrices(), node, false, "prices", "price", "double");
		mapper.addNode(object.getCreate(), node, false, "create");
		mapper.addPrimitiveNode(object.getCharacter(), node, false, "character");
		mapper.addPrimitiveNode(object.isTrust(), node, false, "trust");
		mapper.addNode(object.getBeans(), node, false, "pbeans", "bean", "org.vietspider.serialize.test.CDataAttrBean");
		return node;
	}
}
