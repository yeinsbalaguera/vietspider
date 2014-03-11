/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.vietspider.chars.URLUtils;
import org.vietspider.common.Application;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 30, 2009  
 */
public class XMLTextData {

  protected String documentId = "xml";

//  protected List<XMLResource> resources;

  protected XMLHTMLUtil htmlUtil;
  
  protected int resourceCounter = 0;
  
  public XMLTextData() {
    htmlUtil = new XMLHTMLUtil(this);
  }

  protected void searchResources(ArrayList<XMLResource> resources, HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();

      if(n.isNode(Name.A)) {
        Attributes attributes = n.getAttributes(); 
        Attribute attr ;
        if((attr = attributes.get("href")) == null) continue;
        String address  = attr.getValue();
        if(address == null || address.length() < 1) continue;

        String id  = buildResourceId();

        String name  = getName(n, address);
        if(name == null) name = id;

//        if(resources == null) resources = new ArrayList<XMLResource>();
        resources.add(new XMLResource(id, name, address));
//        return new XMLResource(id, name, address);
      } else if(n.isNode(Name.IMG)) {
        Attributes attributes = n.getAttributes(); 
        Attribute attr ;
        if((attr = attributes.get("src")) == null) continue;
        String address  = attr.getValue();
        if(address == null || address.length() < 1) continue;

        String id  = buildResourceId();

        String name  = getName(null, address);
        if(name == null) name = id;

//        if(resources == null) resources = new ArrayList<XMLResource>();
        resources.add(new XMLResource(id, name, address));
//        return new XMLResource(id, name, address);
      }
    }
  }

  protected String buildResourceId() {
    resourceCounter++;
//    if(resources == null) return "no_id";
//    StringBuilder builder  = new StringBuilder(documentId).append('.').append(resources.size()+1);
    StringBuilder builder  = new StringBuilder(documentId).append('.').append(resourceCounter);
    return builder.toString();
  }


  protected String getName(HTMLNode node, String address) {
    String name = null;
    if(node != null) {
      HTMLText textUtils = new HTMLText();
      StringBuilder builder = new StringBuilder();
      textUtils.buildText(builder, node);
      name  =  builder.toString();

      NodeHandler nodeHandler = new NodeHandler();
      try {
        if(nodeHandler.count(name) < 10) return rename(name);
      } catch (Exception e) {
      }
    }

    if(address.indexOf('/') > -1) {
      name = address.substring(address.lastIndexOf('/')+1);
    } else {
      name = address;
    }

    if(name.indexOf('?') > -1) name = name.substring(0, name.indexOf('?'));

    try{
      name = rename(name);
    } catch (Exception e) {
      return null;
    }
    return name;
  }

  protected String rename(String name) throws Exception {
    char [] chars = URLDecoder.decode(name, Application.CHARSET).toCharArray();
    char [] specs = {'-', '\\', '?', '|', '"', '=', '<', '>'};
    int i = 0;
    while(i < chars.length){
      for(char c : specs){
        if(chars[i] != c) continue;
        chars[i] = '.';
        break;
      }
      i++;
    }
    return new String(chars);
  }

  public String createLink(String refer, String link) {
    URLUtils urlUtils = new URLUtils();
    if(refer.startsWith("http://")
        || refer.startsWith("shttp://") 
        || refer.startsWith("https://")) {
      try {
        return urlUtils.createURL(new URL(refer), link);
      } catch (Exception e) {
      }
    } 
    refer = urlUtils.createURL(refer, link);
    return refer;
  }


}
