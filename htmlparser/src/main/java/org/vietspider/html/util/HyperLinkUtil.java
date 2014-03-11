/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  *
 **************************************************************************/
package org.vietspider.html.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.chars.TextVerifier;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Thuannd
 *         nhudinhthuan@yahoo.com
 * Apr 21, 2006
 */
public class HyperLinkUtil {   
  
  private URLUtils urlCreator;
  private SiteLinkVerifier siteLinkVerifier;
  private ImageLinkVerifier imageLinkVerifier;
  private NormalLinkVerifier normalLinkVerifier;
  private JavaScriptVerifier jsVerifier;
  
  private final static Map<String, String> linkAttributeMap = new HashMap<String, String>(4); 
  private final static Map<String, String> linkAttributeFullMap = new HashMap<String, String>(5);
  private final static Map<String, String> pageAttributeFullMap = new HashMap<String, String>(5);
  
  public HyperLinkUtil() {
    urlCreator  = new URLUtils();
    siteLinkVerifier = new SiteLinkVerifier();
    imageLinkVerifier  = new ImageLinkVerifier();
    normalLinkVerifier = new NormalLinkVerifier();
    jsVerifier = new JavaScriptVerifier();

    linkAttributeMap.put("a", "href");
    linkAttributeMap.put("iframe", "src");
    linkAttributeMap.put("frame", "src");
    linkAttributeMap.put("meta", "url");

    linkAttributeFullMap.put("a", "href");
    linkAttributeFullMap.put("iframe", "src");
    linkAttributeFullMap.put("frame", "src");
    linkAttributeFullMap.put("meta", "url");
    linkAttributeFullMap.put("link", "href");
    linkAttributeFullMap.put("embed", "src");
    
    pageAttributeFullMap.put("a", "href");
    pageAttributeFullMap.put("meta", "url");
    pageAttributeFullMap.put("link", "href");
    pageAttributeFullMap.put("input", "src");
    pageAttributeFullMap.put("iframe", "src");
    pageAttributeFullMap.put("frame", "src");
    pageAttributeFullMap.put("embed", "src");
    pageAttributeFullMap.put("script", "src");
    pageAttributeFullMap.put("img", "src");
    pageAttributeFullMap.put("body", "background");
  }
  
  public void setSiteLinkVerifier(SiteLinkVerifier siteLinkVerifier) {
    this.siteLinkVerifier = siteLinkVerifier;
  }

  public SiteLinkVerifier getSiteLinkVerifier() {
    return siteLinkVerifier;
  }

  public List<String> scanScriptLink(List<String> values, HTMLNode root) {
    IdentifierAttributeHandler handler = new IdentifierAttributeHandler(values, null,  "*", "onclick");
    handler.handle(root);
    
    List<String> list = handler.getValues();
    
    handler = new IdentifierAttributeHandler(list, jsVerifier, "a", "href");
    handler.handle(root);
    
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.SCRIPT)) continue;
      if(n.getChildren().size() < 1) continue;
      list.add(n.getChild(0).getTextValue());
    }
    return list;
  }
 
  @Deprecated()
  public List<String> scanScriptLink(List<NodeImpl> tokens) {
    IdentifierAttributeHandler handler = new IdentifierAttributeHandler(null, null,  "*", "onclick");
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      handler.handle(token);
    }
    
    List<String> list = handler.getValues();
    handler = new IdentifierAttributeHandler(list, jsVerifier, "a", "href");
    
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      handler.handle(token);
    }
    
    return list;
  }
  
  @Deprecated()
  public synchronized List<String> getSiteLink(HTMLNode node) {
    return scanSiteLink(null, node);
  }
  
  public synchronized List<String> scanSiteLink(HTMLNode node) {
    return scanSiteLink(null, node);
  }
  
  public synchronized List<String> scanSiteLink(List<String> values, HTMLNode node) {
    LinkAttributeHandler handler = new LinkAttributeHandler(values, siteLinkVerifier, linkAttributeMap);
    handler.handle(node);
    return handler.getValues();
  }
  
  public synchronized List<String> scanSiteLink(List<NodeImpl> tokens) {
    LinkAttributeHandler handler = new LinkAttributeHandler(null, siteLinkVerifier, linkAttributeMap);
//    MapAttributeHandler handler = new MapAttributeHandler(null, siteLinkVerifier, linkAttributeMap);
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      handler.handle(token);
    }
    return handler.getValues();
  }
  
  public synchronized List<String> scanHomepageLinks(HTMLNode node) {
    Map<String, String> map = new HashMap<String, String>(4); 
    map.put("frame", "src");
    MapAttributeHandler handler = new MapAttributeHandler(null, siteLinkVerifier, map);
    handler.handle(node);
    return handler.getValues();
  }
  
  public List<String> scanImageLink(HTMLNode node) {
    IdentifierAttributeHandler handler = new IdentifierAttributeHandler(null, null, "img", "src");
    handler.handle(node);
    return handler.getValues();
  }
  
  public String scanSingleImageLink(HTMLNode node) { 
    IdentifierAttributeHandler handler =
      new IdentifierAttributeHandler(null, imageLinkVerifier, "img", "src");
    return handler.getAttributeValue(node);
  }   
     
  public  synchronized void createFullNormalLink(HTMLNode node, URL home) {   
    createFullLink(node, linkAttributeFullMap, home, normalLinkVerifier);  
  } 
  
  public  synchronized void createFullImageLink(HTMLNode node, URL home) {   
    createFullLink(node, "img", "src", home, imageLinkVerifier);  
  } 
  
  public synchronized void createFullLink(HTMLNode node, 
      Map<String, String> map, URL home, ValueVerifier verifier) {
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isTag()) createFullSingleLink(n, map, home, verifier); 
    }
  } 
  
  public  synchronized void createFullNormalLink(List<NodeImpl> tokens, URL home) {
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl nodeImpl = tokens.get(i);
      if(!nodeImpl.isTag()) continue;
      createFullSingleLink(nodeImpl, pageAttributeFullMap, home, normalLinkVerifier); 
    }
  } 
  
  private void createFullSingleLink(HTMLNode node, Map<String, String> map,
                                    URL home, ValueVerifier verifier)   {
    Set<String> keys = map.keySet();
    Iterator<String> iter = keys.iterator();
    while(iter.hasNext()){
      String key = iter.next();
      if(node.isNode(key) || (key.length() == 1 && key.charAt(0) == '*')){
        Attributes attrs = node.getAttributes();  
        Attribute attr = attrs.get(map.get(key));
        if(attr == null)  continue;
        String value = attr.getValue();
        if(value == null) continue;
        if(verifier != null && !verifier.verify(value)) continue;
//        System.out.println("truoc "+value);
        value  = urlCreator.createURL(home, value);
//        System.out.println("sau "+value);
        attr.setValue(value);      
        attrs.set(attr);
      }
    }  
  }
  
  public void createFullLink(HTMLNode node, 
      String nodeName, String attrName, URL home, ValueVerifier verifier) {
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isTag()) createFullSingleLink(n, nodeName, attrName, home, verifier);
    }
  } 
  
  private void createFullSingleLink(HTMLNode node, 
      String nodeName, String attrName, URL home, ValueVerifier verifier)   {
    Attribute attr = null;
    if(node.isNode(nodeName) || (nodeName.length() == 1 && nodeName.charAt(0) == '*')) {
      Attributes attrs = node.getAttributes();  
      int idx = attrs.indexOf(attrName);
      if(idx < 0)  return;
      attr = attrs.get(idx);
      String value = attr.getValue();
      if(verifier != null && !verifier.verify(value)) return;
      value  = urlCreator.createURL(home, value);      
      attr.setValue(value);      
      attrs.set(attr);
    }
  }
  
  public static class SiteLinkVerifier extends TextVerifier implements ValueVerifier {
    public boolean verify(String link){
      if(link == null) return false;
      link = link.toLowerCase();    
      String start[]={"mailto", "javascript", "window", "history", "#"};
      String end[]={"css", "js", "jpg", "png", "gif", "jpeg", "bmp", "dat", "exe", "txt", 
                    "java", "pdf", "doc", "xls", "rm", "ram", "wma", "wmv", "mp3", "swf", "zip", "jar", "rar"};
      String exist[] ={"img(\"", "image", ":sendim"} ;
      return !startOrEndOrExist(link, start, end, exist); 
    }
  }
  
  public static class ImageLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String exist[] = {"img", "image"};
      String end[]={"jpg", "gif", "jpeg", "bmp", "dib"};
      return existIn(link, exist) || endWith(link, end);
    }
  }
  
  public static class JavaScriptVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      return link.toLowerCase().startsWith("javascript");    
    }
  }
  
  public static class NormalLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String start[]={"mailto", "javascript", "window", "history"};    
      String exist[] ={"javascript", "#"} ;
      String end[]={};
      return !startOrEndOrExist(link, start, end, exist); 
    }
  }

  public ImageLinkVerifier getImageLinkVerifier() { return imageLinkVerifier; }
  
}

