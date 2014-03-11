/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpExtractor;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2010  
 */
public class PriceExtractor implements INlpExtractor<String> {

  @SuppressWarnings("all")
  public void extract(String id, Collection<?> values, TextElement element) {
    List<Point> points = element.getPoint(type());
    String text = element.getValue();
    List<String> list = (List<String>) values;
    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      StringBuilder builder = new StringBuilder();
      while(point != null) {
        if(point.getScore() > 0) {
          String value  = extract(text, point.getStart(), point.getEnd());
          if(value == null) continue;
          if(builder.length() > 0) builder.append('-');
          builder.append(value);
        }
        point = point.getNext();
      }
      String price = builder.toString(); 
      list.add(price);
//      System.out.println(builder);
    }
    removeDuplicate(list);
  }
  
  private void removeDuplicate(List<String> list) {
    if(list == null || list.size() < 1) return;
    boolean equals = true;
    for(int i = 0; i < list.size(); i++) {
      for(int j = i+1; j < list.size(); j++) {
        if(!list.get(j).equals(list.get(i))) {
          equals = false;
          break;
        }
      }
    }
    if(!equals) return;
    while(list.size() > 1) {
      list.remove(0);
    }
  }
  
  private String extract(String text, int start, int end) {
//    return text.substring(start, end);

//        System.out.println(" text : "+ text.substring(start, end));
    StringBuilder builder = new StringBuilder();
    int i = start;

    boolean white = false;
    while(i < end) {
      char c = text.charAt(i);
      if(c == '(') c = ' ';
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        if(white) {
          i++;
          continue;
        } 
        white = true;
      } else {
        white = false;
      }
      if(Character.isUpperCase(c)) {
        c = Character.toLowerCase(c);
      }
      builder.append(c);
      i++;
    }
    //    System.out.println(" value "+ builder);
    return builder.toString();
  }

  @Override
  public Collection<String> createCollection() {
    return new ArrayList<String>();
  }

  public boolean isExtract(TextElement element) {
    return element.getPoint(type()) != null;
  }

  public short type() { return NLPData.PRICE; }
  
  @SuppressWarnings("unused")
  public void closeCollection(Collection<?> collection) {
  }
}
