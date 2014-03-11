/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.Collection;
import java.util.HashSet;
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
public class PhoneExtractor implements INlpExtractor<String> {

  @SuppressWarnings("all")
  public void extract(String id, Collection<?> values, TextElement element) {
    List<Point> points = element.getPoint(type());
    String text = element.getValue();
    HashSet<String> phones = (HashSet<String>) values;
    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      phones.add(extract(text, point.getStart(), point.getEnd()));
    }
  }
  
  private String extract(String text, int start, int end) {
//    System.out.println(" text : "+ text);
    StringBuilder builder = new StringBuilder();
    for(int i = start; i < end; i++) {
      char c = text.charAt(i);
      if(c == 'o' || c == 'O') c = '0';
      if(Character.isDigit(c)) builder.append(c);
    }
    return builder.toString();
  }

  public boolean isExtract(TextElement element) {
    return element.getPoint(type()) != null;
  }

  public short type() { return NLPData.PHONE; }

  public Collection<String> createCollection() { 
    return new HashSet<String>();
  }
  
  @SuppressWarnings("unused")
  public void closeCollection(Collection<?> collection) {
  }
}
