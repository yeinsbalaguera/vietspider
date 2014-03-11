package org.vietspider.db.log.user;


import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SerializableMapping;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.attribute.Attributes;


public class UserLogs_MappingImpl implements SerializableMapping<UserLogs> {

	private final static int code=31667671;

	public UserLogs create() {
		return new UserLogs();
	}

	public void toField(UserLogs object, XMLNode node, String name, String value) throws Exception {
		if(name.equals("article")) {
			object.setArticleId(XML2Object.getInstance().toValue(String.class, value));
			return;
		}
		if(name.equals("users")) {
			List<UserLog> list = null;
			list = object.getActions();
			if(list == null) list = new ArrayList<UserLog>();
			XML2Object.getInstance().mapCollection(list, UserLog.class, node);
			object.setActions(list);
			return;
		}

	}

	public XMLNode toNode(UserLogs object) throws Exception {
		XMLNode node = new XMLNode("article-user");
		Attributes attrs  = new Attributes(node);
		Object2XML mapper = Object2XML.getInstance();
		mapper.addNode(object.getArticleId(), node, false, "article");
		mapper.addNode(object.getActions(), node, false, "users", "item", "org.vietspider.db.log.user.UserLog");
		return node;
	}
}
