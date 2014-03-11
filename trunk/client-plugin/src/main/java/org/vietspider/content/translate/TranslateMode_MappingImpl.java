package org.vietspider.content.translate;


import org.vietspider.content.translate.TranslateMode;
import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;


public class TranslateMode_MappingImpl implements SerializableMapping<TranslateMode> {

	private final static int code=21659956;

	public TranslateMode create() {
		return new TranslateMode();
	}

	public void toField(TranslateMode object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("mode")) {
			object.setMode(XML2Object.getInstance().toValue(short.class, value));
			return;
		}
		if(name.equals("application-id")) {
			object.setApplicationId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("from")) {
			object.setFrom(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("vi")) {
			object.setTo(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(TranslateMode object) throws Exception {
		XMLNode node = new XMLNode("translate-mode");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addPrimitiveNode(object.getMode(), node, false, "mode");
		mapper.addNode(object.getApplicationId(), node, false, "application-id");
		mapper.addNode(object.getFrom(), node, false, "from");
		mapper.addNode(object.getTo(), node, false, "vi");
		return node;
	}
}
