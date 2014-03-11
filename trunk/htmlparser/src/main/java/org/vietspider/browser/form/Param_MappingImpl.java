package org.vietspider.browser.form;


import org.vietspider.browser.form.Param;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;


public class Param_MappingImpl implements SerializableMapping<Param> {

	private final static int code=17267367;

	public Param create() {
		return new Param();
	}

	public void toField(Param object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("type")) {
			object.setType(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("link_type")) {
			object.setLinkType(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("from")) {
			object.setFrom(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("values")) {
			Set<String> set = null;
			set = object.getValues();
			if(set == null) set = new HashSet<String>();
			XML2Object.getInstance().mapCollection(set, String.class, node);
			object.setValues(set);
			return;
		}

	}

	public XMLNode toNode(Param object) throws Exception {
		XMLNode node = new XMLNode("param");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getType(), node, false, "type");
		mapper.addPrimitiveNode(object.getLinkType(), node, false, "link_type");
		mapper.addPrimitiveNode(object.getFrom(), node, false, "from");
		mapper.addNode(object.getValues(), node, false, "values", "item", "java.lang.String");
		return node;
	}
}
