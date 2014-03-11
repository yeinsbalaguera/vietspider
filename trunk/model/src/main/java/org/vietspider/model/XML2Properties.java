/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.List;
import java.util.Properties;

import org.vietspider.parser.xml.XMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class XML2Properties {
  protected Properties parseProperties(XMLNode node) {
    Properties properties = new Properties();
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1)  return properties;
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(!n.isNode("property")) continue;
      if(n.getChildren() == null || n.getChildren().size() < 2) continue;
      XMLNode key = n.getChild(0);
      if(!key.isNode("key") || key.getChildren() == null || key.getChildren().size() < 1) continue;
      XMLNode value = n.getChild(1);
      if(!value.isNode("value") || value.getChildren() == null || value.getChildren().size() < 1) {
        properties.put(key.getChild(0).getTextValue(), "");
        continue;
      }
      properties.put(key.getChild(0).getTextValue(), value.getChild(0).getTextValue());
    }
    return properties;
  }
}
