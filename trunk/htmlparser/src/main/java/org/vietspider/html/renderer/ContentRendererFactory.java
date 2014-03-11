/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.checker.CheckModel;
import org.vietspider.html.renderer.checker.LinkNodeChecker;
import org.vietspider.html.renderer.checker.NodeChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 18, 2009  
 */
public class ContentRendererFactory {
  
  private static List<HTMLNode> searchBadNodes2(HTMLNode node, List<NodeChecker> checkers) {
    List<HTMLNode> ignores = new ArrayList<HTMLNode>();
    searchBadNodes2(node, ignores, checkers);
    return ignores;
  }
  
  private static  void searchBadNodes2(
      HTMLNode node, List<HTMLNode> ignores, List<NodeChecker> checkers) {
    int maxLevel = 0;
    for(int i = 0 ; i < checkers.size(); i++) {
      int level = checkers.get(i).getLevel();
      if(level > maxLevel) maxLevel = level; 
    }
    searchBadNodes2(node, ignores, checkers, maxLevel);
  }
  
  private static  void searchBadNodes2(
      HTMLNode node, List<HTMLNode> ignores, List<NodeChecker> checkers, int max) {
    /*if(node.isNode(Name.IMG)) {
      HTMLNode table = getAncestor(node, Name.TABLE, 0, 5);
      if(table != null) {
        wrapper.add(table);
//      System.out.println(table.getTextValue());
      }
      return;
    }  */
    
    CheckModel model = new CheckModel(node);
    int level = 0; 
    while(level <= max) {
      for(int i = 0 ; i < checkers.size(); i++) {
        if(!checkers.get(i).isValid(model, level)) {
          //          if(node.getTextValue().indexOf("Năm Mậu Tý đi qua mở") > -1) { 
          //            System.out.println("===================================================");
          //            System.out.println(checkers.get(i).getClass());
          //            System.out.println(node.getTextValue());
          //          }
          ignores.add(model.getRemoveNode());
          return;
        }
      }
      level++;
    }
    
   
    for(int i = 0 ; i < checkers.size(); i++) {
      if(!checkers.get(i).isValid(model, 0)) {
//        System.out.println("=========================================================");
//        System.out.println(node.getTextValue());
//        System.out.println("=========================================================");
        ignores.add(node);
        return;
      }
    }
    if(node == null) return;
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      searchBadNodes2(children.get(i), ignores, checkers);
    }
  }  
  

  public static ContentRenderer createContentRenderer(HTMLNode body, String url) {
    List<NodeChecker> checkers = NodeChecker.createDefaultCheckers(url);
    LinkNodeChecker linkNodeChecker = (LinkNodeChecker)checkers.get(0);
    List<HTMLNode> ignores = searchBadNodes2(body, checkers);
    return new ContentRenderer(body, ignores, linkNodeChecker);
  }

  public static HTMLNode searchBody(HTMLDocument document) throws Exception {
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      node.setValue(chars);              
    }  

    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    NodePath nodePath  = pathParser.toPath("BODY");
    return extractor.lookNode(document.getRoot(), nodePath);
  }
  
}
