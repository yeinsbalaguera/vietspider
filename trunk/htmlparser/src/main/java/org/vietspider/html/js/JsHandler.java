/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.js;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 7, 2008  
 */
public class JsHandler {
  
  public static synchronized void updateDocument(
      HTMLDocument document, List<String> jsDocWriters) throws Exception {

    List<HTMLNode> jsScripts = new ArrayList<HTMLNode>();
    HTMLNode root = document.getRoot();
    if(root.isNode(Name.SCRIPT)) {
      jsScripts.add(root);
    } else {
      searchScriptNode(root.iterator(), jsScripts);
    }
    
    for(int i = 0 ; i < jsScripts.size(); i++) {
      List<HTMLNode> childen = jsScripts.get(i).getChildren();
      if(childen.size() < 1) continue;
      
      List<JsFunction> jsFunctions = null;
      String scriptValue  = childen.get(0).getTextValue();
//      System.out.println(scriptValue);
      try {
        jsFunctions = JsParser.parse(scriptValue);
      } catch (Exception e) {
        StringBuilder builder = new StringBuilder(scriptValue);
        if(e.getMessage() != null) builder.append("\t\t =>").append(e.getMessage());
        throw new Exception(builder.toString());
      }
      if(jsFunctions == null) continue;
        
      StringBuilder builder = new StringBuilder();
      for(int k = 0; k  < jsFunctions.size(); k++) {
        JsFunction js = jsFunctions.get(k); 
        String nameFunc = js.getName().toLowerCase().trim();
        for(int j = 0; j < jsDocWriters.size(); j++) {
          if(nameFunc.equalsIgnoreCase(jsDocWriters.get(j))) {
            if(builder.length() > 0) builder.append('\n');
            builder.append(js.getValue());
            break;
          }
        }
      }
      if(builder.length() < 1) continue;
//      System.out.println(builder.toString());
      replace(jsScripts.get(i), new HTMLParser2().createDocument(builder.toString()));
    }
  }
  
  private static void replace(HTMLNode script, HTMLDocument doc) {
    HTMLNode parent = script.getParent();
    if(parent == null) return;
    List<HTMLNode> childen = parent.getChildren();
    if(childen ==  null) return;
    for(int i = 0; i < childen.size(); i++) {
      if(childen.get(i) != script) continue;
      List<HTMLNode> values = getNodes(doc);
      if(values.size() > 0) {
        parent.setChild(i, values.get(0));
//        childen.set(i, values.get(0));
        for(int k = 1; k < values.size(); k++) {
          parent.addChild(i+k, values.get(k));
//          childen.add(i+k, values.get(k));
        }
      }
      
      return;
    }
  }
  
  private static List<HTMLNode> getNodes(HTMLDocument document) {
    HTMLNode root = document.getRoot();
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    if(root.isNode(Name.HTML))  {
      List<HTMLNode> children = root.getChildren();
      if(children == null) return values;
      for(int i = 0; i< children.size(); i++) {
        if(children.get(i).isNode(Name.HEAD)) continue;
        if(children.get(i).isNode(Name.BODY)) {
          List<HTMLNode> bodyChildren = children.get(i).getChildren();
          if(bodyChildren.size() > 0) values.addAll(bodyChildren);
        } else {
          values.add(children.get(i));
        }
      }
    } else {
      values.add(root);
    }
    return values;
  }
  
  
  private static void searchScriptNode(NodeIterator iterator, List<HTMLNode> jsScripts) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.SCRIPT)) jsScripts.add(n);
    }
    
    /*List<HTMLNode> childen = node.getChildren();
    if (childen == null)  return ;
    for(int i = 0; i < childen.size(); i++) {
      HTMLNode child = childen.get(i); 
      if(child == null) continue;
      if(child.isNode(Name.SCRIPT)) {
        jsScripts.add(child);
        continue;
      } 
      searchScriptNode(child, jsScripts);
    }*/
  }
  
  public static void main(String[] args) throws Exception {
    StringBuilder builder = new StringBuilder("  <div class=\"bText\">");
    builder.append("\n\t\t<script language=\"javascript\"> show_postcontent('a aaaa bbdd\n");
    builder.append("<font face=\"Ariral\"> sdfdshfjdsfds </font> xcvxcvcxdf 333223 ');</script>\n\t");
    builder.append("<a id=\"feedbacks\"></a><a id=\"comments\"></a><a id=\"trackbacks\">");
    builder.append("</a><a id=\"pingbacks\"></a> \n\t");
    builder.append("<h4>Địa chỉ gửi link liên kết đến bài viết này</h4>");
    
    List<String> jsScripts = new ArrayList<String>();
    jsScripts.add("show_postcontent");
    
    HTMLDocument document = new HTMLParser2().createDocument(builder.toString());
    JsHandler.updateDocument(document, jsScripts);
    System.out.println(document.getTextValue());
  }
}
