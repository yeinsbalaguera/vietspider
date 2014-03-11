/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 22, 2007  
 */
public class TestRemoveNode {
  
//  public static void cleanEmptyNode(HTMLNode node) {
//    List<HTMLNode> children  = node.getChildren();
//    if(node.isNode(Name.I)) {
//      System.out.println("==============================================================");
//      System.out.println(node.getChildren().size());
//      System.out.println(node.getTextValue());
//      System.out.println("==============================================================");
//    }
//    if(children == null) return ;
//    for(HTMLNode child : children) {
//      if(child.getChildren() == null || 
//          (child.getChildren() != null && child.getChildren().size() < 1)) {
//        removeNode(child);
//        continue;
//      }
//      cleanEmptyNode(child);
//    }
//  }
  
  private static void searchEmptyNode(HTMLNode node, List<HTMLNode> list) {
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return ;
    for(HTMLNode child : children) {
      if((child.getChildren() != null && child.getChildren().size() < 1)) {
        addEmptyNode(child, list);
        continue;
      }
      searchEmptyNode(child, list);
    }
  }
  
  private static void addEmptyNode(HTMLNode node, List<HTMLNode> list) {
    if(node.isNode(Name.COMMENT) || node.isNode(Name.STYLE) || node.isNode(Name.SCRIPT)) return;
    if(node.getConfig().end() == Tag.FORBIDDEN) return ;
    System.out.println("=========================================" + node.getConfig().toString());
    System.out.println(node.getTextValue()+"\n");
    HTMLNode parent = node.getParent();
    while(parent.getParent().getChildren().size() < 2) {
       if(parent.getParent() == null) break;
       parent  = parent.getParent();
     }
     System.out.println(parent.getChildren().get(0).getTextValue());
     if(parent == node.getParent()) {
       list.add(node); 
     } else {
       list.add(parent);
     }
  }
  
  public static void removeNode(HTMLNode node) {
    if(node == null) return ;
    HTMLNode parent = node.getParent();
    if(parent == null || parent.getChildren() == null) return ;
    parent.removeChild(node);
//    node.setParent(null);
    node.setValue(new char[]{});
   
    if(parent.getChildren().size() < 1) {
      System.out.println(parent.getTextValue());
      removeNode(parent); 
    }
  }
  
  public static void main(String[] args) throws Exception {
    URL url = new URL("http://thuannd:9247/vietspider/DETAIL/200710221502593591");
    HTMLDocument document = HTMLParser.createDocument(url.openStream(),"utf-8");
    
//    HTMLDocument document = HTMLParser.createDocument(new File("F:\\Temp2\\TestHTMLParser\\TestRemoveEmpty.html"), "utf-8");
    HTMLNode root = document.getRoot();

    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    NodePath nodePath = pathParser.toPath("BODY[0]");
    HTMLNode subNode = extractor.lookNode(root, nodePath);
//    System.out.println(subNode.getChildren().size());
//    System.out.println(subNode.getTextValue());
//    removeNode(subNode);
    
    List<HTMLNode> emptyNodes = new LinkedList<HTMLNode>();
    searchEmptyNode(subNode, emptyNodes);
    for(HTMLNode ele : emptyNodes) {
      removeNode(ele);
    }
//    cleanEmptyNode(root);
    
    File file = new File("F:\\Temp2\\TestHTMLParser\\TestRemoveEmpty2.html");    
    RWData.getInstance().save(file, root.getTextValue().getBytes("utf-8"));
    
    
  }
}
