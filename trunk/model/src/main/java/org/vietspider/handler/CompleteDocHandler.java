/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vietspider.chars.URLUtils;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2009  
 */
public class CompleteDocHandler {
  
  protected HyperLinkUtil hyperLinkUtil;
  protected URLUtils urlUtils;
  protected Map<String,String> linkMap = new HashMap<String,String>();
  
  public CompleteDocHandler(HyperLinkUtil hyperLinkUtil, URLUtils urlCreator) {
    this.urlUtils = urlCreator;
    this.hyperLinkUtil = hyperLinkUtil;
    
    linkMap.put("body", "background");
    linkMap.put("link","href");
    linkMap.put("script","src");
    linkMap.put("embed","src");
    linkMap.put("a","href");
  }
  
  public CompleteDocHandler() {
    this.urlUtils = new URLUtils();
    this.hyperLinkUtil = new HyperLinkUtil();
  }
  
  public HTMLNode completeTable(HTMLNode root){
    if(root.isNode(Name.TD) || root.isNode(Name.TH)){
      Attributes attributes = root.getAttributes();
      Attribute attr = attributes.get("width");
      if(attr != null) attributes.remove(attr);

      NodeImpl tr =  new NodeImpl("tr".toCharArray(), Name.TR, TypeToken.TAG);
      tr.setIsOpen(false);
      tr.addChild(root);
      root = tr;
    }

    if(root.isNode(Name.TR) || root.isNode(Name.TBODY) 
        || root.isNode(Name.THEAD) || root.isNode(Name.TFOOT)){

      Attributes attributes = root.getAttributes();
      Attribute attr = attributes.get("width");
      if(attr != null) attributes.remove(attr);

      NodeImpl table =  new NodeImpl("table".toCharArray(), Name.TABLE, TypeToken.TAG);
      table.setIsOpen(false);
      table.addChild(root);
      root = table;
    }   
    return root;
  }

  public String completeURL(String address, List<NodeImpl> list) {
    URL home = null;
    try{
      home = new URL(address);
    } catch (Exception e) {
      return address;
    }
//    if(home == null) return address;

    String sourceAddress = urlUtils.createURL(home, address);

    for(int i = 0; i < list.size(); i++) {
      HTMLNode node = list.get(i);
      hyperLinkUtil.createFullNormalLink(node, home);
      hyperLinkUtil.createFullImageLink(node, home);
      hyperLinkUtil.createFullLink(node, linkMap, home, null);
    }

    return sourceAddress;
  }  

  public String completeURL(String address, HTMLNode root) {
    URL home = null;
    try{
      home = new URL(address);
    } catch (Exception e) {
      return address;
    }
//    if(home == null) return address;

    String sourceAddress = urlUtils.createURL(home, address);
    
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      hyperLinkUtil.createFullNormalLink(node, home);
      hyperLinkUtil.createFullImageLink(node, home);
      hyperLinkUtil.createFullLink(node, linkMap, home, null);
    }

    return sourceAddress;
  }  
}
