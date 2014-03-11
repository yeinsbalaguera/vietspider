/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:50 PM
 */

package org.vietspider.html.parser;

import org.vietspider.html.HTMLDocument;
import org.vietspider.token.TokenParser.Factory;
/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class CharsToken extends Factory<NodeImpl> { 
  
  private HTMLDocument document;
  
  public CharsToken() {
    this.document = new HTMLDocument();
  }
  
  public CharsToken(HTMLDocument document) {
    this.document = document;
  }
  
  public int create(char [] data, int start, int end, int type, Object...params){
//    System.out.println("bebe " + new String(Arrays.copyOfRange(data, start, end)));
    return ((TokenCreator)params[0]).create(data, start, end, type, null);
  }

  public HTMLDocument getDocument() { return document; }
  
//  private int endComment(char [] data, int start, int end) {
//    if(start >= end) return end;
//    if(start > data.length) return data.length;   
//    CharsUtil.cutAndTrim(data, start, Math.min(end, data.length)); 
//    return end;
//  }

}
