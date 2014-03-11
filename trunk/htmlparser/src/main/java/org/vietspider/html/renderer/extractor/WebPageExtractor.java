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
public class WebPageExtractor extends WebExtractHandler {

  public HTMLNode extract(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    if(children == null) return node;
    while(children.size() == 1) {
      node = node.getChild(0);
      children = node.getChildren();
      if(children == null || children.size() < 1) return node;
    }
    
    if(children == null || children.size() < 1) return node;
    
    HTMLNode[] nodes = new HTMLNode[children.size()];
    AnalyticsRenderer [] renderers = new AnalyticsRenderer[nodes.length];
    for(int i = 0; i < children.size(); i++) {
      nodes[i] = children.get(i);
      renderers[i] = new AnalyticsRenderer(nodes[i], false);
    }
   
    int i = 0;
    for(;i < renderers.length; i++) {
      if(renderers[i].getWord() >1 ) break; 
    }
    
    int max = i;
    for(;i < renderers.length; i++) {
      if(renderers[i].compare(renderers[max]) > 0) {
        max = i;
      }
    }
    
    if(max  < 0) return node;
    
    for(i = 0; i < max; i++) {
      if(renderers[i] == null) continue;
//      System.out.println(renderers[i].getWord());
//      System.out.println(renderers[i].getTextValue());
      if(renderers[i].getWord() < 1) {
        renderers[i] = null;
        removeNode(nodes[i]);
      }
    }
    
    if(max >= renderers.length) return node;
    
    int maxScore = renderers[max].getScore();
    
    for(i = max+1; i < renderers.length; i++) {
      if(renderers[i] == null) continue;
      
      if(renderers[i].getParagraph() < 2 
          && renderers[i].getWord() < 30) {
//        System.out.println(renderers[i].getTextValue());
//        System.out.println(renderers[i].getParagraph() + " : " + renderers[i].getSentence() + " : " + renderers[i].getWord());
//        System.out.println(renderers[i].getTextValue());
//        System.out.println(renderers[i].getScore() + " : "+ renderers[max].getScore());
        
        renderers[i] = null;
        removeNode(nodes[i]);
      } else if(maxScore > 0) {
        int rate = (renderers[i].getScore()*100)/maxScore;
//      System.out.println(renderers[i].getScore() + " : "+ renderers[max].getScore() + " : "+ rate);
        if(rate < 10) {
          renderers[i] = null;
          removeNode(nodes[i]);
        }
      }
    }
    
    return node;
    //phan tich cu phap cai ni
  }
  
}
