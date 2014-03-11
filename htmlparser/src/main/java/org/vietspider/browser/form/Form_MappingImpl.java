package org.vietspider.browser.form;


import org.vietspider.browser.form.Form;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;
import org.vietspider.browser.form.Param;


public class Form_MappingImpl implements SerializableMapping<Form> {

	private final static int code=13349923;

	public Form create() {
		return new Form();
	}

	public void toField(Form object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("name")) {
			object.setName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("action")) {
			object.setAction(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("method")) {
			object.setMethod(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("params")) {
			List<Param> list = null;
			list = object.getParams();
			if(list == null) list = new ArrayList<Param>();
			XML2Object.getInstance().mapCollection(list, Param.class, node);
			object.setParams(list);
			return;
		}

	}

	public XMLNode toNode(Form object) throws Exception {
		XMLNode node = new XMLNode("form");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getName(), node, false, "name");
		mapper.addNode(object.getAction(), node, false, "action");
		mapper.addNode(object.getMethod(), node, false, "method");
		mapper.addNode(object.getParams(), node, false, "params", "param", "org.vietspider.browser.form.Param");
		return node;
	}
}
