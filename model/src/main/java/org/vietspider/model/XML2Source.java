/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class XML2Source extends XML2Properties {
  
  private XML2Region xmlRegion = new XML2Region();
  
  public Source toSource(byte [] bytes) throws Exception {
    String xml = new String(bytes, Application.CHARSET);
    if(xml == null || xml.trim().length() < 1) return null;
    
    //old verion
//    XMLDocument document = XMLParser.createDocument(xml, null);
//    return XML2Object.getInstance().toObject(Source.class, document);
    
    //new version
    Source source = new Source();
    XMLDocument document = XMLParser.createDocument(bytes, Application.CHARSET, null);
    invokeNode(document.getRoot(), source);
    return source;
  }
  
  public void invoke(XMLDocument document, Source source) {
    invokeNode(document.getRoot(), source);
  }
  
  private void invokeNode(XMLNode node, Source source) {
    if(node.isNode("source")) {
      Attributes attributes = AttributeParser.getInstance().get(node);
      invoke(attributes, source);
      List<XMLNode> children = node.getChildren();
      if(children == null) return;
      for(int i = 0; i < children.size(); i++) {
        invokeData(children.get(i), source);
      }
    } else {
      List<XMLNode> children = node.getChildren();
      if(children == null) return;
      for(int i = 0; i < children.size(); i++) {
        invokeNode(children.get(i), source);
      }
    }
  }
  
  private void invokeData(XMLNode node, Source source) {
    /*if(node.isNode("group")) {
      List<XMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) {
        source.setGroup("ARTICLE");
      } else {
        source.setGroup(children.get(0).getTextValue());
      }
    } else*/
    if(node.isNode("category")) {
      List<XMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) {
        source.setCategory("category_0");
      } else {
        source.setCategory(children.get(0).getTextValue());
      }
    } else if(node.isNode("name")) {
      List<XMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) {
        source.setName("name_0");
      } else {
        source.setName(children.get(0).getTextValue());
      }
    } else if(node.isNode("home")) {
      source.setHome(toHomes(node));
    } else if(node.isNode("pattern")) {
      List<XMLNode> children = node.getChildren();
      if(children != null && children.size() > 0) {
        source.setPattern(children.get(0).getTextValue());
      }
    } else if(node.isNode("crawl-times")) {
      source.setCrawlTimes(toCrawlTimes(node));
//      System.out.println(" tai day co "+ source.getCrawlTimes());
    } else if(node.isNode("update-regions")) {
//      System.out.println("thu nhat "+node.getName());
      Region region = parseRegion(node.getChildren(), 0);
      if(region == null) {
        region = new Region("region:update");
        region.setType(Region.DEFAULT);
      }
      source.setUpdateRegion(region);
    } else if(node.isNode("extract-regions")) {
//      System.out.println("thu nhat "+node.getName());
      source.setExtractRegion(parseRegions(node.getChildren()));
    } else if(node.isNode("process-regions")) {
//      System.out.println("thu nhat "+node.getName());
      source.setProcessRegion(parseRegions(node.getChildren()));
    } else if(node.isNode("properties")) {
      source.setProperties(parseProperties(node));
    }
  }
  
  private void invoke(Attributes attributes, Source source) {
    Attribute attr = attributes.get("encoding");
    if(attr != null) source.setEncoding(attr.getValue());
    
    attr = attributes.get("depth");
    if(attr != null && attr.getValue() != null) {
      try {
        source.setDepth(Integer.parseInt(attr.getValue()));
      } catch (Exception e) {
        source.setDepth(1);
      }
    }
    
    attr = attributes.get("priority");
    if(attr != null && attr.getValue() != null) {
      try {
        source.setPriority(Integer.parseInt(attr.getValue()));
      } catch (Exception e) {
        source.setPriority(48);
      }
    }
    
    attr = attributes.get("source-type");
    if(attr != null && attr.getValue() != null) {
      source.setGroup(attr.getValue());
    } else {
      source.setGroup("ARTICLE");
    }
    
    attr = attributes.get("extract-type");
    if(attr != null && attr.getValue() != null) {
      try {
        source.setExtractType(ExtractType.valueOf(attr.getValue()));
      } catch (Exception e) {
        source.setExtractType(ExtractType.NORMAL);
      }
    }
    
    attr = attributes.get("lastModified");
    if(attr != null && attr.getValue() != null) {
      try {
        source.setLastModified(Long.parseLong(attr.getValue()));
      } catch (Exception e) {
      }
    }
  }
  
  private String[] toHomes(XMLNode node) {
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1)  return new String[0];
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(!n.isNode("url")) continue;
      if(n.getChildren() == null || n.getChildren().size() < 1) continue;
      values.add(n.getChild(0).getTextValue());
    }
    return values.toArray(new String[values.size()]);
  }
  
  private String[] toCrawlTimes(XMLNode node) {
//    System.out.println(node.getName());
    List<XMLNode> children = node.getChildren();
//    System.out.println(children + " : "+ (children != null ? children.size() : " null " ));
    if(children == null || children.size() < 1)  return null;
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(!n.isNode("time")) continue;
      if(n.getChildren() == null || n.getChildren().size() < 1) continue;
//      System.out.println(" tai day co "+ n.getChild(0).getTextValue());
      values.add(n.getChild(0).getTextValue());
    }
    return values.toArray(new String[values.size()]);
  }
  
  private Region[] parseRegions(List<XMLNode> children) {
    List<Region> values = new ArrayList<Region>();
    for(int i = 0; i < children.size(); i++) {
      Region region = xmlRegion.invokeNode(children.get(i));
      if(region != null) values.add(region);
    }
    return values.toArray(new Region[values.size()]);
  }
  
  private Region parseRegion(List<XMLNode> children, int idx) {
//    System.out.println(" thay co "+ idx + " / "+ children.size());
    if(children == null || idx >= children.size()) return null;
    return xmlRegion.invokeNode(children.get(idx));
  }
  
  
}
