/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 2, 2007
 */
public class HTMLNodeUtilBak extends HTMLNodePath {
  
  @Deprecated()
  public void searchTextNode(HTMLNode node, List<HTMLNode> list, boolean checkLength) {
    if(node.isNode(Name.SCRIPT) || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return;
    if(node.isNode(Name.CONTENT)) {
      char [] values = node.getValue();
      CharacterUtil wordUtils = new CharacterUtil();
      if(checkLength && values.length < 20  && wordUtils.count(values) < 1) return;
      list.add(node);
      return;
    }
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) return ;
    for(HTMLNode ele : children){
      searchTextNode(ele, list, checkLength) ;
    }
  }
  
  private boolean isEmpty(char [] chars) {
    if(chars.length > 100) return true;
    for(char ele : chars) {
      if(Character.isWhitespace(ele) || Character.isSpaceChar(ele)) continue;
      return false;
    }
    return true;
  }
  
  @Deprecated()
  public void searchTextNode(HTMLNode node, List<char[]> list){
    if(node == null) return ;
    if(node.isNode(Name.SCRIPT) || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return;
    
    if(node.isNode(Name.CONTENT)) {
      if(!isEmpty(node.getValue())) list.add(node.getValue());
      return ;
    }
    
    List<HTMLNode> childen = node.getChildren();
    if (childen == null)  return ;
    for(HTMLNode ele : childen) {
      searchTextNode(ele, list);      
    }
  }
  
  
  public String getCommonIndexPath(HTMLNode element1, HTMLNode element2) {
    if(element1 == null || element1 == null) return null;
    String path1 = getIndexPath(element1);
    String path2 = getIndexPath(element2);
    return getCommonIndexPath(path1, path2);
  }  
  
  @Deprecated()
  public void searchAnchors(HTMLNode node, List<HTMLNode> anchors) {
    if(node.isNode(Name.A)) {
      anchors.add(node);
      return;
    }
    List<HTMLNode> children = node.getChildren();
    if(children == null) return ;
    for(HTMLNode ele : children) {
      searchAnchors(ele, anchors);
    }
  }
  
  @Deprecated()
  public List<HTMLNode> search(HTMLNode root, Name name) {
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    search(root, values, name);
    return values;
  }

  @Deprecated()
  public void search(HTMLNode root, List<HTMLNode> values, Name name) {
    if(root.isNode(name)) {
      values.add(root);
      return;
    }
    
    List<HTMLNode> children = root.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      search(children.get(i), values, name);
    }
  }
  
}
