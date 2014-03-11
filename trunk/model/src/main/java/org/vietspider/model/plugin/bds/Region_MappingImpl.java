package org.vietspider.model.plugin.bds;


import org.vietspider.token.attribute.*;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;


public class Region_MappingImpl implements SerializableMapping<Region> {

	private final static int code=12677476;

	public Region create() {
		return new Region();
	}

	public void toField(Region object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("region-name")) {
			object.setRegionName(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("region-id")) {
			object.setRegionId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Region object) throws Exception {
		XMLNode node = new XMLNode("region");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getRegionName(), node, false, "region-name");
		mapper.addNode(object.getRegionId(), node, false, "region-id");
		return node;
	}
}
