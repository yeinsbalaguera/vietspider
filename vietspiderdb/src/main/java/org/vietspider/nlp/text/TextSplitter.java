/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.text;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TextSplitter extends Splitter<TextElement> {

  public TextElement create(String seq, TextElement current) {
    TextElement element = new TextElement(seq);
    if(current == null) return element;
    current.next = element;
    element.previous = current;
    return element;
  }
  
}
