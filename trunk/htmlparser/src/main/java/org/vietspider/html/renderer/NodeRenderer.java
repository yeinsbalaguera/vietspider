/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.HTMLParentUtils;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2009  
 */
public class NodeRenderer {
  
  private int start;
  private int end;
  private List<HTMLNode> nodes;
  private ContentRenderer renderer;
  
  private int maxPattern = -1;
  private int minPattern = -1;
  
  private int timePattern = 0;
  private int totalSeparator = 0;
  
  private int totalSentence = 0;
  private int totalWord = 0;
  private int score = 0;
  
  private List<HTMLNode> contents;
  private HTMLNode parent;
  
  public NodeRenderer(ContentRenderer renderer,
       List<HTMLNode> nodes, int start, int end) {
    this.renderer = renderer;
    this.nodes = nodes;
    this.start = start;
    this.end = end;
    
    analytics();
  }
  
  public List<HTMLNode> getNodes() { return nodes; }
  public void setNodes(List<HTMLNode> nodes) { this.nodes = nodes; }
  
  public int getStart() { return start; }
  public void setStart(int start) { this.start = start; }
  
  public int getEnd() { return end; }
  public void setEnd(int end) { this.end = end;  }
  
  public ContentRenderer getRenderer() { return renderer; }
  public void setRenderer(ContentRenderer renderer) { this.renderer = renderer; }
  
  public NodeRenderer[] split(String pattern) {
    if(pattern == null || pattern.length() < 1) {
      return new NodeRenderer[]{this};
    }
    
    String text = renderer.getTextValue();
    int index = text.indexOf(pattern, start);
    
    if(index >= end) return new NodeRenderer[]{this};
    
    List<NodeRenderer> list = new ArrayList<NodeRenderer>();
    
    int s = start;
    
    while(index > -1 && s < end) {
      List<HTMLNode> subNodes = renderer.getNodePositions(s, index);
      list.add(new NodeRenderer(renderer, subNodes, s, index));
      s = index + pattern.length();
      index = text.indexOf(pattern, s);
    }
    
    if(s < end) {
      index = end;
      List<HTMLNode> subNodes = renderer.getNodePositions(s, index);
      list.add(new NodeRenderer(renderer, subNodes, s, index));
    }
    
    return list.toArray(new NodeRenderer[list.size()]);
  }  
  
  public String getText() { return renderer.getTextValue().substring(start, end); }
  
  public int getScore() {  return score; }
  
  private void analytics() {
    if(maxPattern > -1) return;
    
    TextCounter textCounter = new TextCounter();
    int s = start;
    int index = start;
    String text = renderer.getTextValue();
    int pattern = 0;
    maxPattern = 0;
    minPattern = -1;
    
    while(index < end) {
      char c = text.charAt(index);
      if(c == '\\') {
        //tinh toan diem cho phan truoc
        int sentence = textCounter.countSentence(text, s, index);
        int word = textCounter.countWord(text, s, index);
//        if(sentence > 5) {
//          System.out.println("========================================");
//          System.out.println(text.substring(s, index));
//          System.out.println(" ===== > "+ sentence+  " : "
//              + word + " : "+ calculate(pattern, sentence, word));
//          System.out.println("========================================");
//        }
        score += new ScoreCalculator().calculate(pattern, sentence, word);
        //end ket thuc
        pattern = 0;
        while(c == '\\') {
          pattern++;
          index++;
          if(index >= end) break;
          c = text.charAt(index);
        }
        s = index;
        if(pattern > maxPattern) maxPattern = pattern;
        if(pattern < minPattern || minPattern < 0) minPattern =  pattern;
        timePattern++;
        totalSeparator += pattern;
      }
      index++;
    }
    
    if(s < index) {
      int sentence = textCounter.countSentence(text, s, index);
      int word = textCounter.countWord(text, s, index);
      score += new ScoreCalculator().calculate(pattern, sentence, word);
    }
    
//    System.out.println("================================================================");
    for(int i = 0;  i < nodes.size(); i++) {
      HTMLNode n = nodes.get(i);
      if(n.isNode(Name.OBJECT))  {
        score += 1000;
      } else if(n.isNode(Name.IMG))  {
//        System.out.println(new String(n.getValue()));
        Attributes attributes = n.getAttributes();
        score += calculateFromAttr(attributes.get("width"));
        score += calculateFromAttr(attributes.get("height"));
      } 
    }
    
  }
  
  private int calculateFromAttr(Attribute attribute) {
    if(attribute == null) return 0;
    try {
      int size = Integer.parseInt(attribute.getValue().trim());
      if(size < 500) return 0;
      return size*500;
    } catch (Exception e) {
    }
    return 0;
  }
  
  
  public int getTotalSentence() { return totalSentence; }
  
  public int getTotalWord() { return totalWord; }

  public int getTimePattern() { return timePattern; }
  
  public int getTotalPattern() { return totalSeparator; }
  
  public int getMaxPattern() { return maxPattern; }
  
  public String getMaxPatternValue() {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < maxPattern; i++) {
      builder.append('\\');
    }
    return builder.toString();
  }
  
  public int compare(NodeRenderer nodeRenderer) {
//    int value = nodeRenderer.getTotalSentence() - totalSentence;
//    if(value > 5) return -1;
//    if(value < -5) return 1;
    
//   int value = nodeRenderer.getScore() - score;
//    if(value > 1000) return -1;
//    if(value < -1000) return 1;
//    
//    return totalWord - nodeRenderer.getTotalWord(); 
    return score*totalSentence - nodeRenderer.getScore()*nodeRenderer.getTotalSentence();
  }
  
  public int getOverage() {
    int overage = 0;
    int time = timePattern;
    int total = totalSeparator;
    if(time > 0) overage = total/time;
    return overage;
  }
  
  public int comparePattern() {
    int mid = (maxPattern + minPattern)/2;
    int time = timePattern;
    int total = totalSeparator;
    
    int overage = 0;
    if(time > 0) overage = total/time;
//    System.out.println("thay co "+ overage + " : " + mid);
    return Math.abs(mid - overage);
  }

  public int getMinPattern() { return minPattern; }

  public List<HTMLNode> getContents() {
    if(contents != null) return contents;
    contents = renderer.getNodePositions(start, end);
    return contents;  
  }
  
  public HTMLNode getParent() {
    if(parent != null) return parent;
    HTMLParentUtils parentUtil = new HTMLParentUtils();
    parent = parentUtil.getUpParent(getContents());
    return parent;
  }

  
}

