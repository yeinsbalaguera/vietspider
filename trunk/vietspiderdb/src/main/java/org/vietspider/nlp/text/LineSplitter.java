/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.text;

import java.util.ArrayList;
import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2010  
 */
public class LineSplitter {

  public TextElement split(String text) {
    List<String> elements = split2List(text);
    if(elements.size() < 1) return null;
    TextElement current = new TextElement(elements.get(0));
    TextElement root = current;
    for(int i = 1; i < elements.size(); i++) {
      current  = create(elements.get(i), current);
    }
    return root;
  }
  
  private List<String> split2List(String value) {
    int start  = 0;
    int index  = 0;
    List<String> list = new ArrayList<String>();
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == '\n' || c == '\r') {
        String element = value.substring(start, index); 
        if(element.trim().length() > 0) list.add(element);
        start = index+1;
      }
      index++;
    }
    if(start < value.length()) {
      String element = value.substring(start, value.length()); 
      if(element.trim().length() > 0) list.add(element);
    }
    return list;
  }


  private TextElement create(String seq, TextElement current) {
    TextElement element = new TextElement(seq);
    if(current == null) return element;
    current.next = element;
    element.previous = current;
    return element;
  }
}
