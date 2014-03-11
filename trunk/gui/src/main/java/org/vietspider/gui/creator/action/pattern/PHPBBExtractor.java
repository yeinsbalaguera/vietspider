/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action.pattern;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.path2.PathUtils;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2009  
 */
public class PHPBBExtractor {

  private List<String> extractPaths = new ArrayList<String>();
  private String pagePath;
  private String titlePath;
  private String userPath = null;
  private String postPath = null;

  public void extract(HTMLNode root) {
    NodeIterator iterator = root.iterator();

    NodePathParser pathParser = new NodePathParser();

    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.DIV)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("id");
      if(attribute == null) continue;
      String value = attribute.getValue();
      if(value == null) continue;

      if("pagecontent".equalsIgnoreCase(value)
          || "page-body".equalsIgnoreCase(value)) {
        String threadPath = pathParser.toPath(node).toString();
        extractPaths.add(threadPath); 

        //search user node
        userPath = searchPath(threadPath, node, new String[]{"postauthor", "author"});
        
        //search post node
        postPath = searchPath(threadPath, node, new String[]{"postbody"}, Name.DIV);

        pagePath = searchPageNode(threadPath, node);
        if(pagePath != null) {
          pagePath = node.getName()+"[" + String.valueOf(extractPaths.size()-1) + "]" + pagePath;
        }
      } else if("pageheader".equalsIgnoreCase(value)
          || "page-header".equalsIgnoreCase(value)) {
        titlePath = node.getName()+"[" + String.valueOf(extractPaths.size()) + "]";
        extractPaths.add(pathParser.toPath(node).toString()); 
      }
    }
  }

  private String searchPageNode(String threadPath, HTMLNode root) {
    List<HTMLNode> children = root.getChildren();
    if(children == null) return null;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode node = children.get(i);
      if(hasContainer(node)) {
        String path = searchPageNode(threadPath, node);
        if(path != null) return path;
        continue;
      }
      
      if(!isPageList(node)) continue;
      node = upParent(node, Name.TD, Name.DIV);

      NodePathParser pathParser = new NodePathParser();
      String path = pathParser.toPath(node).toString();
      try {
        path = path.substring(threadPath.length());
        return path;
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
    return null;
  }
  
  private boolean hasContainer(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.TD) 
          || node.isNode(Name.DIV)) return true;
    }
    return false;
  }
  
  private boolean isPageList(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.A)) continue;
      Attributes attributes = n.getAttributes();
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      String value = attribute.getValue();
      if(value == null) continue;
      if(value.indexOf("start=") < 0) continue;
      if(n.getChildren() == null || n.getChildren().size() < 1) continue;
      try {
        Integer.parseInt(buildText(n.getChild(0).getTextValue()));
        return true;
      } catch (Exception e) {
      }
    }
    return false;
  }
  
  private String buildText(String text) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      }
      index++;
    }
    return builder.toString();
  }
  
  private String searchPath(String threadPath, HTMLNode root, String [] clazzes, Name...names) {
    List<HTMLNode> nodes = null;
    String clazz = null;
    int max = 2;
    while(max > 0) {
      for(int i = 0;i < clazzes.length; i++) {
        nodes = searchNode(root, clazzes[i], names);
        if(nodes.size() >= max) {
          clazz = clazzes[i];
          break;
        }
      }
      max--;
    }
    if(nodes == null || nodes.size() < 1) return null;
    List<String> paths = new ArrayList<String>();
    NodePathParser pathParser = new NodePathParser();
    for(HTMLNode node : nodes) {
      String path = pathParser.toPath(node).toString();
      path = path.substring(threadPath.length());
      path = root.getName()+"[" + String.valueOf(extractPaths.size()-1) + "]" + path;
      paths.add(path);
    }
    return PathUtils.normalize(paths) + "[class=" + clazz + "]";
  }
  
  private List<HTMLNode> searchNode(HTMLNode root, String clazz, Name...names) {
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();
    
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!isName(node.getName(), names)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("class");
      if(attribute == null) continue;
      String value = attribute.getValue();
      if(value == null) continue;
      value = value.toLowerCase();
      if(value.indexOf(clazz) < 0) continue;
      nodes.add(node);
    }
    return nodes;
  }
  
  private boolean isName(Name name, Name...names) {
    if(names == null || names.length < 1) return true; 
    for(int i = 0; i < names.length; i++) {
      if(name == names[i]) return true;
    }
    return false;
  }


  private HTMLNode upParent(HTMLNode node, Name...names) {
    if(node == null) return null;
    for(int i = 0; i < names.length; i++) {
      if(node.isNode(names[i])) return node;
    }
    return upParent(node.getParent(), names);
  }

  public List<String> getExtractPaths() { return extractPaths; }

  public String getPagePath() { return pagePath; }

  public String getTitlePath() { return titlePath; }

  public String getUserPath() { return userPath; }

  public String getPostPath() { return postPath; }
  
  //http://97vippro.vnbb.com/viewtopic.php?f=82&t=1185
}
