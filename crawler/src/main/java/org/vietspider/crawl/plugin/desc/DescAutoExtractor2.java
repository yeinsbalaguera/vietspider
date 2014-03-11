/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public class DescAutoExtractor2 extends DescExtractor {
  
  private int minDesc = 30;
  private int maxDesc = 70;
  
  public DescAutoExtractor2(HTMLExtractor extractor, NodeHandler nodeHandler) {
    super(extractor, nodeHandler);
  }
  
  public String extract(HTMLNode root, List<HTMLNode> contents) {
    TextRenderer renderer = new TextRenderer(root, contents, TextRenderer.HANDLER, true);
    
    String text = renderer.getTextValue().toString().trim();
    if(text.length() < 20) return text;
    
    String desc = cutDesc(contents, renderer, renderer.getTextValue().toString(), 0, 0);
    /*if(desc.length() < 10) {
      java.io.File file  = new java.io.File("D:\\Temp\\desc\\" 
          +  String.valueOf(System.currentTimeMillis()) + ".txt");
      try {
        RWData.getInstance().save(file, renderer.getTextValue().toString().getBytes("utf-8"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }*/
    
    return desc;
    
  }
  
  private String cutDesc(List<HTMLNode> contents, 
      TextRenderer renderer, String value, int start, int counter) {
    if(counter >= 5) return "...";
    
    while(start < value.length()) {
      char c = value.charAt(start);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        start++;
        continue;
      }
      break;
    }
    
    int idx = indexOf(value, start);
    
    if(idx < 0) {
      if(value.length() < 100) {
        idx = value.length();
      } else {
        idx = indexOf(value, start, '\n');
        if(idx < 0) idx = start;
      }
    } else {
      idx++;
    }
//    System.out.println("============ >thay co "+ idx+ " : |"
//        + value.charAt(idx)+"|"+value.substring(idx-5, idx+5)+"|"+ " aaaaaaaa");
    String desc = value.substring(start, idx);
    List<HTMLNode> removes = renderer.getNodePositions(start, idx);
//  System.out.println("=== >"+removes.size());
    
//    if(isNotDesc(removes)) {
//      return cutDesc(contents, renderer, value, idx+1, counter+1);
//    }
   
    if(remove) removeNodes(contents, removes);
    return desc;
  }
  
  private static int indexOf(String value, int start) {
    int idx1 = indexOf(value, start, '.');
    int idx2 = indexOf(value, start, '?');
    int idx3 = indexOf(value, start, '!');
    if(idx1 > start && idx2 > start && idx3 > start) {
      return Math.min(idx1, Math.min(idx2, idx3));
    } else if(idx1 > start && idx2 > start) {
      return Math.min(idx1, idx2);
    } else if(idx1 > start && idx3 > start) {
      return Math.min(idx1, idx3);
    }
    return idx1;
  }
  
  private static int indexOf(String value, int start, char _char) {
    int idx = value.indexOf(_char, start + 5);
    while(idx > -1 && idx < value.length() - 1) {
      char c = value.charAt(idx+1);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
      idx = value.indexOf(_char, idx + 5);
    }
    return idx;
  }
  
//  private boolean isNotDesc(List<HTMLNode> nodes) {
//    int counter = 0;
//    for(int i = 0; i < nodes.size(); i++) {
//      HTMLNode node = nodes.get(i);
//      if(isLinkTag(node, 0)) continue;
//      counter += nodeHandler.count(new String(node.getValue()));
//    }
//    return counter < 5; 
//  }
//  
//  private boolean isLinkTag(HTMLNode node, int counter) {
//    if(node == null || counter >= 2) return false;
//    if(node.isNode(Name.A)) return true;
//    return isLinkTag(node.getParent(), counter+1);
//  }
  
  public int getMinDesc() { return minDesc; }
  public void setMinDesc(int minDesc) { this.minDesc = minDesc; }

  public int getMaxDesc() { return maxDesc; }
  public void setMaxDesc(int maxDesc) { this.maxDesc = maxDesc; }
  
  /*private int[] cutDesc(String value) {
    int start = 0;
    while(start < value.length()) {
      char c = value.charAt(start);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        start++;
        continue;
      }
      break;
    }
    
    int idx = value.indexOf('.', start + 5);
    while(idx > -1 && idx < value.length() - 1) {
      char c = value.charAt(idx+1);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
      idx = value.indexOf('.', idx + 5);
    }
    if(idx > 0) return new int[]{start, idx + 1};
    idx = value.indexOf('\n');
    if(idx > 0) return new int[]{start, idx + 1};
    return new int[]{idx, value.length()};
  }
  */
  
  public static void main(String[] args) throws Exception  {
    int start = 0;
    File file = new File("D:\\Temp\\desc\\" + "1312265297485" + ".txt");
    String value = new String(RWData.getInstance().load(file), Application.CHARSET);
    while(start < value.length()) {
      char c = value.charAt(start);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        start++;
        continue;
      }
      break;
    }
    
    System.out.println(" value length "+ value.length());
    
    System.out.println(" ==  >"+ start);
    
    int idx = indexOf(value, start);
    
    if(idx < 0) {
      if(value.length() < 100) {
        idx = value.length();
      } else {
        idx = indexOf(value, start, '\n');
        if(idx < 0) idx = start;
      }
    } else {
      idx++;
    }
//    if(idx < 0) idx = value.indexOf('.', start);
    System.out.println("=====  > "+ idx);
    while(idx > -1 && idx < value.length() - 1) {
      char c = value.charAt(idx+1);
      System.out.println(idx + "|" + c + "|");
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
      idx = value.indexOf('.', idx + 5);
    }
    
    System.out.println(value.substring(start, idx));
  }
}
