/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.extractor;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.content.AnalyticsRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class WebPageSearcher extends WebExtractHandler {

  public HTMLNode searchContentNode(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    if(children == null || children.size() < 1) {
//      System.out.println(" no children");
      return node;
    }
    
    AnalyticsRenderer renderer = new AnalyticsRenderer(node, false);
    if(renderer.getBlock() < 1) {
//      System.out.println(" no block");
      return node;
    }
    if(renderer.getData() > 2) {
//      System.out.println(" has content");
      return node;
    }

    HTMLNode[] nodes = new HTMLNode[children.size()];
    for(int i = 0; i < children.size(); i++) {
      nodes[i] = children.get(i);
    }

    AnalyticsRenderer [] renderers = new AnalyticsRenderer[nodes.length];
   
    int max = -1;
    for(int i = 0; i < nodes.length; i++) {
      renderers[i] = new AnalyticsRenderer(nodes[i], false);
      
//      System.out.println(" ==================================================");
//      System.out.println(" thay co cai ni "+ i + " : " + renderers[i].getParagraph());
//      System.out.println(renderers[i].getTextValue());
      
      if(max  < 0) {
        max = i;
      } else if(renderers[i].compare(renderers[max]) > 0) {
        max = i;
      }
    }
    
    if(max < 0) {
//      System.out.println(" no max");
      return node;
    }
    
    int data = renderers[max].getData();
    int block = renderers[max].getBlock();
    if(block < 1) {
//      System.out.println(" no block");
      return node;
    }
    
//    System.out.println(" ==================================================");
//    System.out.println(" thay co cai ni " + node.getName() + " : " + data + " : "+ max + " : " + renderers[max].getParagraph());
//    System.out.println(renderers[max].getTextValue());
    
    int rate = (data*100)/block;
    if(rate > 40) {
//      System.out.println(" has rate");
      return nodes[max]; 
    }
    return searchContentNode(nodes[max]);
  }
  

}
