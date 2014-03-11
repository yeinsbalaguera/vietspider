/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.List;
import java.util.Map;

import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLNode;
import org.vietspider.js.JsUtils;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 27, 2009  
 */
public class LinkAttributeHandler extends MapAttributeHandler { 

  public LinkAttributeHandler(List<String> list, ValueVerifier verifier, Map<String, String> map) {
    super(list, verifier, map);
  }

  @Override()
  public void handleNode(HTMLNode n) {
    switch (n.getName()) {
    case A:
      searchOnclick(n); 
      break;
    case META:
      searchMetaRefresh(n);
      break;
    case SCRIPT:
      searchLocationLink(n);
      break;
    case CODE_CONTENT:
      searchContentLink(n);
      break;
      //    case BODY:
      //      searchBodyLocation(n);
      //      break;

      //    case FRAME:
      //      searchFrameValue(n);
      //      break;
    default:
      break;
    }
  }

  private void searchOnclick(HTMLNode n) {
    Attributes attributes = n.getAttributes();
    Attribute attribute = attributes.get("onclick");
    if(attribute == null || attribute.getValue() == null) return ;
    String jsFunction = attribute.getValue();
    String [] params = JsUtils.getParams(jsFunction);
    for(int i = 0 ; i < params.length; i++) {
      list.add(params[i]);
    }
  }

  private void searchMetaRefresh(HTMLNode n) {
    Attributes attributes = n.getAttributes();
    Attribute attribute = attributes.get("http-equiv");
    if(attribute == null || attribute.getValue() == null) return ;

    if(!"refresh".equalsIgnoreCase(attribute.getValue().trim())) return ;

    attribute = attributes.get("content");
    if(attribute == null) return ;
    String link = attribute.getValue();

    if(link == null) return;
    link = cleanLink(link.trim());
    if(link == null) return ;
    list.add(link);
  }

  //  private void searchFrameValue(HTMLNode n) {
  //    Attributes attributes = AttributeParser.getInstance().get(n);
  //    Attribute attribute = attributes.get("src");
  //    if(attribute == null || attribute.getValue() == null) return ;
  //    list.add(attribute.getValue());
  //  }

  private void searchLocationLink(HTMLNode n) {
    List<HTMLNode> children = n.getChildren();
    if(children == null || children.size() < 1) return;
    String value = children.get(0).getTextValue();
    Attribute[] attributes = AttributeParser.getInstance().get(value);
    //    System.out.println(attributes.length);
    for(int i = 0; i < attributes.length; i++) {
      String name = attributes[i].getName();
//      System.out.println(name);
      if("document.location.href".equalsIgnoreCase(name)
          || "window.location".equalsIgnoreCase(name)
          || "location.href".equalsIgnoreCase(name)
          || "window.location.href".equalsIgnoreCase(name)
          || "document.location".equalsIgnoreCase(name)
          || "location".equalsIgnoreCase(name)
      ) {
//        System.out.println(" thay co "+attributes[i].getValue());
        list.add(attributes[i].getValue());
        return;
      } 
    }
  }

  private String cleanLink(String link) {
    if(link == null || link.length() < 2) return null;
    int index = link.toLowerCase().indexOf("=");
    if(link.charAt(link.length() - 1) == ';') {
      link = link.substring(0, link.length() - 1);
    }

    link = link.substring(index+1).trim();
    if(link.length() < 1) return null;
    if(link.charAt(0) == '\"' || link.charAt(0) == '\'') {
      link = link.substring(1);
    }

    if(link.charAt(link.length() - 1) == '\"' 
      || link.charAt(link.length() - 1) == '\'') {
      link = link.substring(0, link.length() - 1);
    }
    return link;
  }

  private void searchContentLink(HTMLNode n) {
    String value = new String(n.getValue());
    value = value.trim();
    String pattern = "document.location.href";
    int index = value.indexOf(pattern);
    if(index < 0) {
      pattern = "window.location";
      index = value.indexOf(pattern);
    }
    if(index < 0) {
      pattern = "location.href";
      index = value.indexOf(pattern);
    }
    if(index < 0) {
      pattern = "window.location.href";
      index = value.indexOf(pattern);
    }

    if(index < 0) {
      pattern = "document.location";
      index = value.indexOf(pattern);
    }

    if(index != 0) return;
    value = value.substring(pattern.length()).trim();
    value = cleanLink(value.trim());
    if(value == null) return ;
    list.add(value);
  }

  private void searchBodyLocation(HTMLNode n) {
    //    System.out.println(value);
    Attributes attributes = n.getAttributes();
    //    System.out.println(attributes.length);
    Attribute attribute = attributes.get("onload");
    if(attribute == null || attribute.getValue() == null) return ;
    String [] params = JsUtils.getParams(attribute.getValue());
    for(String param : params) {
      System.out.println(param);
    }
  }
}
