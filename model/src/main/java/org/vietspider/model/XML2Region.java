/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class XML2Region extends XML2Properties {
  
  Region invokeNode(XMLNode node) {
//    System.out.println("thu 2 "+node.getTextValue());
    if(node.isNode("region")) {
      List<XMLNode> children = node.getChildren();
//      System.out.println(" children "+ children);
      if(children == null) return null;
//      System.out.println(" children co "+ children.size());
      Region region = new Region();
      for(int i = 0; i < children.size(); i++) {
        invokeData(children.get(i), region);
      }
      if(region.getName() == null || region.getName().length() < 1) {
        Attributes attributes = AttributeParser.getInstance().get(node);
        Attribute attribute = attributes.get("name");
//        System.out.println(" === >"+ attribute.getValue());
        if(attribute != null) {
          String name = attribute.getValue();
          if(name != null && (name = name.trim()).length() > 0) {
            region.setName(attribute.getValue());
          }
        }
      }
      return region;
    } 
    
    List<XMLNode> children = node.getChildren();
    if(children == null) return null;
    for(int i = 0; i < children.size(); i++) {
      Region region = invokeNode(children.get(i));
      if(region != null) return region;
    }
    return null;
  }
  
  private void invokeData(XMLNode node, Region region) {
//    System.out.println("=======> "+node.getTextValue());
    if(node.isNode("name")) {
      List<XMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) {
        region.setName("");
      } else {
        region.setName(children.get(0).getTextValue().trim());
      }
    } else if(node.isNode("type")) {
      List<XMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) {
        region.setType(Region.DEFAULT);
      } else {
        try {
          region.setType(Integer.parseInt(children.get(0).getTextValue().trim()));
        } catch (Exception e) {
          region.setType(Region.DEFAULT);
        }
      }
    } else if(node.isNode("paths")) {
      region.setPaths(parsePaths(node));
    } else if(node.isNode("properties")) {
      region.setProperties(parseProperties(node));
    }
  }
  
  private String[] parsePaths(XMLNode node) {
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1)  return new String[0];
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(!n.isNode("item")) continue;
      if(n.getChildren() == null || n.getChildren().size() < 1) continue;
      String text = n.getChild(0).getTextValue().trim();
      if(text.startsWith("<![CDATA[")) {
        text = text.substring(9, text.length()-3); 
      }
      values.add(text);
    }
    return values.toArray(new String[values.size()]);
  }
  
}
