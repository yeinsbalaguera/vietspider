/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 12, 2007  
 */
public abstract class HTMLNodePath {
  
  protected String getIndexPath(HTMLNode element){
    HTMLNode parent = element.getParent();
    HTMLNode child = element;
    StringBuilder path = new StringBuilder();
    while(parent != null){
      if(path.length() > 0) path.insert(0, '.');      
      path.insert(0, parent.getChildren().indexOf(child));
      child = parent;
      parent = parent.getParent();
    }
    return path.toString();
  }
  
  public HTMLNode getNodeByIndex(HTMLNode node, String indexPath) throws Exception {   
    if(indexPath == null || indexPath.trim().length() < 1) {
      throw new NullPointerException("path is empty or null"); 
    }
    String [] split = indexPath.split("\\.");
    HTMLNode ele = node;  
    for(String element : split) {
      ele = node.getChildren().get(Integer.parseInt(element));
      if(ele.getChildren() == null) break;
      node = ele;     
    }
    return ele;
  }  
  
  protected String getCommonIndexPath(String indexPath1, String indexPath2){
    int idx = 0;
    while(idx < Math.min(indexPath1.length(), indexPath2.length())){
      if(indexPath1.charAt(idx) != indexPath2.charAt(idx)) break;
      idx++;
    }
    String indexPath = indexPath1.substring(0, idx);
    if(indexPath.lastIndexOf('.') < 0) return indexPath;
    indexPath = indexPath.substring(0, indexPath.lastIndexOf('.'));
    return indexPath;
  }
}
