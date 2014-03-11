/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 2, 2007
 */
public class HTMLNodeUtil extends HTMLNodePath {
  
  @Deprecated()
  @SuppressWarnings("unused")
  public void searchTextNode(HTMLNode node, List<HTMLNode> list, boolean checkLength) {
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(list, node, verify);
  }
  
  @Deprecated()
  public void searchTextNode(HTMLNode node, List<char[]> list){
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();  
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) return;
      if(verify.isValid(n.getValue())) list.add(n.getValue());
    }
  }
  
  public String getCommonIndexPath(HTMLNode element1, HTMLNode element2) {
    if(element1 == null || element1 == null) return null;
    String path1 = getIndexPath(element1);
    String path2 = getIndexPath(element2);
    System.out.println("thay path 1 " + path1);
    System.out.println("thay path 2 " + path2);
    return getCommonIndexPath(path1, path2);
  }  
  
  public void searchAnchors(HTMLNode node, List<HTMLNode> anchors) {
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) anchors.add(n);
    }
  }
  
  public List<HTMLNode> search(HTMLNode root, Name name) {
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) values.add(n);
    }
    return values;
  }

  public void search(HTMLNode root, final List<HTMLNode> values, Name name) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) values.add(n);
    }
  }
  
}
