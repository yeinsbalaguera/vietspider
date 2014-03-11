/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.extractor;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.CSSData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.content.AnalyticsRenderer;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 14, 2009  
 */
public class WebPageDataSearcher {

  private HTMLDocument document;
  private List<HTMLNode> values = new ArrayList<HTMLNode>();
  
  public WebPageDataSearcher(HTMLDocument document) {
    this.document = document;
  }
  
  public HTMLNode search(HTMLNode node) {
    values.clear();
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) {
//      System.out.println(" tra ve do child");
      return null;
    }
    for(int i = 0; i < children.size(); i++) {
      searchNodes(children.get(i));
    }
    
    if(values.size() < 1) {
//      System.out.println(" tra ve do ko gia tri");
      return null;
    }
    
    AnalyticsRenderer [] renderers = new AnalyticsRenderer[values.size()];
    int max = -1;
    for(int i = 0; i < renderers.length; i++) {
      renderers[i] = new AnalyticsRenderer(values.get(i), false);
//      System.out.println(" ==================================================");
//      System.out.println(" thay co cai ni "+ i + " : " + renderers[i].getParagraph());
//      System.out.println(renderers[i].getTextValue());
      if(max  < 0) {
        max = i;
      } else if(renderers[i].compare(renderers[max]) > 0) {
        max = i;
      }
    }
    
    if(max < 0) {
//      System.out.println(" tra ve do max");
      return null;
    }
    node = values.get(max);
    while(true) {
      HTMLNode newNode = search(node);
      if(newNode == null) return node;
      node = newNode;
    }
    
////    if(node.isNode(Name.TABLE)) {
//      System.out.println(" chay buoc nua "+ node.hashCode());
//      HTMLNode newNode = search(node);
//      if(newNode == null) return node;
//      System.out.println(" ==== chay buoc nua "+ node.hashCode());
////    }
//      return search(newNode);
//    return node;
  }
  
  public void searchNodes(HTMLNode node) {
    if(node == null) return;
    switch (node.getName()) {
//    case TABLE:
//      if(isGreater(node, 550, 50)) {
//        values.add(node);
//        return;
//      }
//      break;
    case DIV:
      if(isGreater(node, 5000, 50)) {
        values.add(node);
        return;
      }
      break;
    case TD:
      if(isGreater(node, 400, 50)) {
        values.add(node);
        return;
      }
      break;
    default:
      break;
    }
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      searchNodes(children.get(i));
    }
  }
  
  public boolean isGreater(HTMLNode node, int width, int rate) {
    Attributes attributes = getAttributes(node);
    Attribute attribute = attributes.get("width");
    if(attribute == null) return false;
    String value = attribute.getValue();
    if(value == null) return false;
    value = value.trim();
    /*if(value.endsWith("%")) {
      value = value.substring(0, value.length()-1);
      try {
        return Integer.parseInt(value) > rate;
      } catch (Exception e) {
        return false;
      }
    }*/
    if(value.endsWith("px")) {
      value = value.substring(0, value.length()-2); 
    }
    try {
      return Integer.parseInt(value) > width;
    } catch (Exception e) {
      return false;
    }
  }
  
  public Attributes getAttributes(HTMLNode node) {
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("class");
    if(attribute == null) return attributes;
    String clazzName  = attribute.getValue();
    if(clazzName == null) return attributes;
    CSSData cssData = document.getResource("CSS.DATA");
    if(cssData == null) return attributes;
    String [] cssValues = cssData.getValue(clazzName);
    for(String css : cssValues) {
//      System.out.println(css);
      AttributeParser.getInstance().getStyle(attributes, css);
    }
    return attributes;
  }
}
