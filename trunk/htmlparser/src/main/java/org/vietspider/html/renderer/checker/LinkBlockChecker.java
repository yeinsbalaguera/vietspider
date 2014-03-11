/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.renderer.content.AnalyticsRenderer;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class LinkBlockChecker {
  
  protected ContentChecker contentChecker;
  
  public LinkBlockChecker(ContentChecker contentChecker) {
    this.contentChecker = contentChecker;
  }
  
  boolean isLink(CheckModel model) {
    HTMLNode node = model.getNode();
    List<HTMLNode> links = new ArrayList<HTMLNode>();
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) links.add(n);
      else if(n.isNode(Name.DIV) 
          || n.isNode(Name.SPAN)) {
        if(hasOnclick(n)) {
          links.add(n);
        }
      }
    }
    
    model.setTotalOfLink(links.size());
    
    if(links.size() > 15) {
      if(!hasParagraph(node, 2)) {
        if(model.getNode().getTextValue().indexOf("XUÂN YÊU") > -1) { 
          System.out.println("===================================================" );
          System.out.println(model.getNode().getTextValue());
        }
        return true;
      }
//      return true;
    } 
    
    if(links.size() >= 3) {
      if(!hasParagraph(node, 1)) return true;  
    }
    
    
    int rate = compareNodes(links);
//    if(model.getNode().getTextValue().indexOf("u tượng của phần mền trong control panel") > -1) { 
//    System.out.println("===================================================" + rate);
//    System.out.println(model.getNode().getTextValue());
//    }
//    if(rate > 0) {
//      System.out.println("================================================");
//      System.out.println("ti le cai nay " + rate);
//      System.out.println(model.getNode().getTextValue());
//    }
    if(rate >= 75) {
      if(hasBlockLink(node)) {
//        if(model.getNode().getTextValue().indexOf("XUÂN YÊU") > -1) { 
//          System.out.println("===================================================" + rate);
//          System.out.println(model.getNode().getTextValue());
//        }
        return false; 
      }
      return true;
    }
    
    if(rate < 40) {
      return false;
    }
//    int countWord = 
    return false;
  }
  
  private boolean hasOnclick(HTMLNode node) {
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("onclick");
    if(attribute == null) return false;
    String value  = attribute.getValue();
    if(value == null || (value = value.trim()).isEmpty()) return false;
    return true;
  }
  
  private int compareNodes(List<HTMLNode> links) {
    if(links.size() < 3) return 0;
    Attributes attributes = links.get(0).getAttributes();
    int counter = 1;
    for(int i = 1; i < links.size(); i++) {
      Attributes attributes1 = links.get(i).getAttributes();
      if(compareAttributes(attributes, attributes1)) counter++;
    }
    return (counter*100)/links.size() ;
  }
  
  private boolean compareAttributes(Attributes attributes1, Attributes attributes2) {
    if(attributes1.size() != attributes2.size()) return false;
    for(int i = 0; i < attributes1.size(); i++) {
      Attribute attr1 = attributes1.get(i);
      Attribute attr2 = attributes2.get(i);
      String name = attr1.getName();
      if(isIgnoreAttribute(name)) continue;
      if(!name.equalsIgnoreCase(attr2.getName()))  return false;
      if(isIgnoreAttributeValue(name)) continue;
      if(!attr1.getValue().equalsIgnoreCase(attr2.getValue()))  return false;
    }
    return true;
  }
  
  private boolean isIgnoreAttribute(String name) {
    if("href".equalsIgnoreCase(name)) return true;
    if("onclick".equalsIgnoreCase(name)) return true;
    return false;
  }
  
  private boolean isIgnoreAttributeValue(String name) {
    if("class".equalsIgnoreCase(name)) return false;
    return true;
  }
  
  public boolean hasParagraph(HTMLNode node, int max) {
    AnalyticsRenderer renderer = new AnalyticsRenderer(node, true);
    if(renderer.getParagraph() < 1) return false;

    String [] elements = renderer.getTextValue().toString().split("\n");
    TextCounter textCounter = contentChecker.getTextCounter();
    for(String element : elements) {
      int counter = textCounter.countSentence(element);
      if(counter > max) return true;

      counter = textCounter.countWord(element, 0, element.length());
      if(counter >= 15) return true;
    }
    return false;
  }
  
  private boolean hasBlockLink(HTMLNode node) {
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return false;
    for(int i = 0; i < children.size(); i++) {
      switch (children.get(i).getName()) {
      case DIV:
      case TABLE:
        if(isBlockLink(children.get(i))) return true;
        break;
      default:
        break;
      }
    }
    for(int i = 0; i < children.size(); i++) {
      if(hasBlockLink(children.get(i))) return true;
    }
    return false;
  }
  
  private boolean isBlockLink(HTMLNode node) {
    NodeIterator iterator = node.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) counter++;
    }
    return counter > 3;
  }
  
}

