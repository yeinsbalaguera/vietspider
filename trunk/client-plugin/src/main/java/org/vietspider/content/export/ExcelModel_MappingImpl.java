package org.vietspider.content.export;


import org.vietspider.content.export.ExcelModel;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import java.util.*;


public class ExcelModel_MappingImpl implements SerializableMapping<ExcelModel> {

	private final static int code=4613007;

	public ExcelModel create() {
		return new ExcelModel();
	}

	public void toField(ExcelModel object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("domain")) {
			object.setDomainId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("page")) {
			object.setPage(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("headers")) {
			List<XMLNode> list = node.getChildren();
			if(list == null) return;
			String[] values = new String[node.getChildren().size()];
			for(int i = 0; i < list.size(); i++) {
				XMLNode n = list.get(i);
				if(n.getChildren() == null || n.getChildren().size() < 1) {
					continue;
				}
				values[i] = XML2Object.getInstance().toValue(String.class, n.getChild(0).getTextValue());
			}
			object.setHeaders(values);
			return;
		}

	}

	public XMLNode toNode(ExcelModel object) throws Exception {
		XMLNode node = new XMLNode("excel-model");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getDomainId(), node, false, "domain");
		mapper.addPrimitiveNode(object.getPage(), node, false, "page");
		mapper.addNode(object.getHeaders(), node, false, "headers", "item", "java.lang.String");
		return node;
	}
}
