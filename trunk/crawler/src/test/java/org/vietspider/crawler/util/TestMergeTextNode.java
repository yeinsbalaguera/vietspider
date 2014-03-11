/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.crawl.plugin.handler.MergeTextNode;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2008  
 */
public class TestMergeTextNode {
  
  protected static List<HTMLNode> searchTextNodes(HTMLNode root) {
    NodeHandler nodeHandler = new NodeHandler();
    List<HTMLNode> refsNode = new ArrayList<HTMLNode>();
    nodeHandler.searchNodes(root.iterator(), refsNode, Name.A);
    if(refsNode.size() > 20) return null;
    
    List<HTMLNode> contentsNode = new ArrayList<HTMLNode>();
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(contentsNode, root, verify);
    
//    nodeHandler.searchTextNode(root, contentsNode);
//    System.out.println("step 6 "+href.getUrl()+ " : "+ contentsNode.size());
    return contentsNode.size() < 2 ? null : contentsNode ;
  }
  
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp\\merge_text_node.html");
    HTMLNode node = new HTMLParser2().createDocument(file,"utf-8").getRoot();
    
    List<HTMLNode> contentNodes = searchTextNodes(node);
    
    for (int i = 0; i < contentNodes.size(); i++) {
      System.out.println(contentNodes.get(i).getTextValue());
      System.out.println("--------------------------------------------------");
    }
    
    MergeTextNode mergeTextNode = new MergeTextNode();
    mergeTextNode.mergeText(contentNodes);
    
    System.out.println("\n\n\n Va sau do \n\n\n");
    for (int i = 0; i < contentNodes.size(); i++) {
      System.out.println(contentNodes.get(i).getTextValue());
      System.out.println("--------------------------------------------------");
    }
  }
  
}
