/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  *
 **************************************************************************/
package org.vietspider.html.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.chars.TextVerifier;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Thuannd
 *         nhudinhthuan@yahoo.com
 * Apr 21, 2006
 */
@Deprecated()
public class HyperLinkUtilBak extends AttributeUtil {   
  
  private URLUtils urlCreator;
  private SiteLinkVerifier siteLinkVerifier;
  private ImageLinkVerifier imageLinkVerifier;
  private NormalLinkVerifier normalLinkVerifier;
  private JavaScriptVerifier jsVerifier;
  
  private final static Map<String, String> linkAttributeMap = new HashMap<String, String>(4); 
  private final static Map<String, String> linkAttributeFullMap = new HashMap<String, String>(5); 
  
  public HyperLinkUtilBak() {
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
  }
  
  public List<String> scanScriptLink(HTMLDocument document) {
    List<String> list = getAttributes(document, null, "*", "onclick", null);
    list.addAll(getAttributes(document, null, "a", "href", jsVerifier));
    return list;
  }
  
  public List<String> scanScriptLink(List<NodeImpl> tokens) {
    List<String> values  = new ArrayList<String>();
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      getAttributes(token, values, "*", "onclick", null);
      values.addAll(getAttributes(token, null, "a", "href", jsVerifier));
    }
    return values;
  }
  
  public synchronized List<String> getSiteLink(HTMLNode node) {
    return getAttributes(node, null, linkAttributeMap, siteLinkVerifier);
  }
  
  public synchronized List<String> getSiteLink(HTMLDocument document) {
    return getAttributes(document, null, linkAttributeMap, siteLinkVerifier);
  }
  
  public synchronized List<String> getSiteLink(List<NodeImpl> tokens) {
    List<String> values  = new ArrayList<String>();
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      getAttributes(token, values, linkAttributeMap, siteLinkVerifier);       
    }
    return values;
  }
  
  public synchronized List<String> getHomepageLinks(HTMLNode node) {
    Map<String, String> map = new HashMap<String, String>(4); 
    map.put("frame", "src");
    return getAttributes(node, null, map, siteLinkVerifier);
  }
  
  public List<String> getImageLink(HTMLNode node) {
    return getAttributes(node, null, "img", "src", null);
  }
  
  public String getSingleImageLink(HTMLNode node) { 
    return getAttribute(node, "img", "src", imageLinkVerifier);
  }   
     
  public  synchronized void createFullNormalLink(HTMLNode node, URL home) {   
    createFullLink(node, linkAttributeFullMap, home, normalLinkVerifier);  
  } 
  
  public  synchronized void createFullImageLink(HTMLNode node, URL home) {   
    createFullLink(node, "img", "src", home, imageLinkVerifier);  
  } 
  
  public  synchronized void createFullLink(HTMLNode node, 
      Map<String, String> map, URL home, ValueVerifier verifier) {
    if(node == null) return;
    createFullSingleLink(node, map, home, verifier);    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children) {
      createFullLink(ele, map, home, verifier);
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
        Attribute attr = attrs.get("src");
        if(attr == null)  continue;
        String value = attr.getValue();
        if(verifier != null && !verifier.verify(value)) continue;
        value  = urlCreator.createURL(home, value);
        attr.setValue(value);      
        attrs.set(attr);
      }
    }  
  }
  
  public void createFullLink(HTMLNode node, 
      String nodeName, String attrName, URL home, ValueVerifier verifier) {   
    createFullSingleLink(node, nodeName, attrName, home, verifier);    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children) {
      createFullLink(ele, nodeName, attrName, home, verifier);
    }
  } 
  
  private void createFullSingleLink(HTMLNode node, 
      String nodeName, String attrName, URL home, ValueVerifier verifier)   {
    Attribute attr = null;
    if(node.isNode(nodeName) || (nodeName.length() == 1 || nodeName.charAt(0) == '*')) {
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
  
  
  public static class SiteLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      if(link == null) return false;
      link = link.toLowerCase();    
      String start[]={"mailto", "javascript", "window", "history", "#"};
      String end[]={"css", "js", "jpg", "gif", "jpeg", "bmp", "dat", "exe", "txt",
                    "java", "pdf", "doc", "rm", "ram", "wma", "wmv", "mp3", "swf", "zip", "jar", "rar"};
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

