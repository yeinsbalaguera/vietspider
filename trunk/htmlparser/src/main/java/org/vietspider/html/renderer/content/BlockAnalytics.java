/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.content;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.checker.ContentChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class BlockAnalytics {
  
  private ContentChecker contentChecker = new ContentChecker(); 
  
  public void filter(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    
    HTMLNode[] nodes = new HTMLNode[children.size()];
    for(int i = 0; i < children.size(); i++) {
      nodes[i] = children.get(i);
    }
    
    List<AnalyticsModel> models = new ArrayList<AnalyticsModel>(nodes.length);
    int maxScore = 0;
    
    TextCounter textCounter = new TextCounter();
    ScoreCalculator scoreCalculator = new ScoreCalculator();
    for(int i = 0; i < nodes.length; i++) {
      if(isTextElement(nodes[i])) return;
      else if(isBlockElement(nodes[i])) {
        AnalyticsRenderer renderer = new AnalyticsRenderer(nodes[i], true);
        String text = renderer.getTextValue().toString();
        int sentence = textCounter.countSentence(text, 0, text.length());
        int word = textCounter.countWord(text, 0, text.length());

        int score = scoreCalculator.calculate(sentence, word);
        models.add(new AnalyticsModel(nodes[i], score));
        if(score > maxScore) maxScore = score;
      }
    }
    
    for(int i = 0; i <  models.size(); i++) {
      int score = models.get(i).getScore();
      int rate = (score*100)/maxScore;
    }
    
    //phan tich cu phap cai ni
  }
  
//  private String 
  
  private boolean isBlockElement(HTMLNode node) {
    switch (node.getName()) {
    case DIV:
    case TABLE:
      return true;
    default:
      return false;
    }
  }
  
  private boolean isTextElement(HTMLNode node) {
    switch (node.getName()) {
    case CONTENT:
    case SPAN:
    case P:
      return contentChecker.isTextBlock(node, false, 10, 2);
    default:
      return false;
    }
  }
  
}
