package org.vietspider.model;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class Group_MappingImpl implements SerializableMapping<Group> {

	private final static int code=22546263;

	public Group create() {
		return new Group();
	}

	public void toField(Group object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("group-type")) {
			object.setType(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("percent-relation")) {
			object.setMinPercentRelation(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("date-range-relation")) {
			object.setDateRangeRelation(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("download-image")) {
			object.setDownloadImage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("force-download-image")) {
			object.setForceDownloadImage(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("set-image-to-meta")) {
			object.setSetImageToMeta(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("start-time")) {
			object.setStartTime(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("end-time")) {
			object.setEndTime(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("download-in-range-time")) {
			object.setDownloadInRangeTime(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("check-title")) {
			object.setCheckTitle(XML2Object.getInstance().toValue(boolean.class, value));
			return;
		}
		if(name.equals("max-priority")) {
			object.setMaxPriority(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("min-priority")) {
			object.setMinPriority(XML2Object.getInstance().toValue(int.class, value));
			return;
		}
		if(name.equals("process-regions")) {
			List<Region> list = null;
			list = object.getProcessRegions();
			if(list == null) list = new ArrayList<Region>();
			XML2Object.getInstance().mapCollection(list, Region.class, node);
			object.setProcessRegions(list);
			return;
		}
		if(name.equals("remote")) {
			object.setRemote(XML2Object.getInstance().toValue(String.class, value));
			return;
		}

	}

	public XMLNode toNode(Group object) throws Exception {
		XMLNode node = new XMLNode("group");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getType(), node, false, "group-type");
		mapper.addPrimitiveNode(object.getMinPercentRelation(), node, false, "percent-relation");
		mapper.addPrimitiveNode(object.getDateRangeRelation(), node, false, "date-range-relation");
		mapper.addPrimitiveNode(object.isDownloadImage(), node, false, "download-image");
		mapper.addPrimitiveNode(object.isForceDownloadImage(), node, false, "force-download-image");
		mapper.addPrimitiveNode(object.isSetImageToMeta(), node, false, "set-image-to-meta");
		mapper.addPrimitiveNode(object.getStartTime(), node, false, "start-time");
		mapper.addPrimitiveNode(object.getEndTime(), node, false, "end-time");
		mapper.addPrimitiveNode(object.isDownloadInRangeTime(), node, false, "download-in-range-time");
		mapper.addPrimitiveNode(object.isCheckTitle(), node, false, "check-title");
		mapper.addPrimitiveNode(object.getMaxPriority(), node, false, "max-priority");
		mapper.addPrimitiveNode(object.getMinPriority(), node, false, "min-priority");
		mapper.addNode(object.getProcessRegions(), node, false, "process-regions", "", "org.vietspider.model.Region");
		mapper.addNode(object.getRemote(), node, false, "remote");
		return node;
	}
}
