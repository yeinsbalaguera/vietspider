/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.text;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 15, 2011  
 */
public abstract class Splitter <T> {
  
  public T split(String text) {
    return split(text, -1);
  }

  public T split(String text, int max) {
    T root = null;
    T current = null;
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      if(max > 0 
          && i - start > max
          && !Character.isLetterOrDigit(c)) {
        
        String seq = text.substring(start, i);
        start = i+1;
        if(!seq.isEmpty()) {
          current = create(seq, current);
          if(root == null) root = current;
        }
        i++;
        continue;
      }
      switch (c) {
      case '\r':
      case '\n':
        String seq = text.substring(start, i);
        start = i+1;
        if(!seq.isEmpty()) {
          current = create(seq, current);
          if(root == null) root = current;
        }
        break;
      case '.':
      case '?':
      case '!':
        if(i < length-2 && 
            (Character.isWhitespace(text.charAt(i+1))
                 || Character.isSpaceChar(text.charAt(i+1))) &&
          (Character.isDigit(text.charAt(i+2))
              || (Character.isLetter(text.charAt(i+2)) 
                  && Character.isUpperCase(text.charAt(i+2))))
            ) {
          seq = text.substring(start, i);
          start = i+1;
          if(!seq.isEmpty()) {
            current = create(seq, current);
            if(root == null) root = current;
          }
        }
        break;
      default:
        break;
      }
      i++;
    }

    i = Math.min(length, i);
    String seq = text.substring(start, i);
    if(!seq.isEmpty()) {
      current = create(seq, current);
      if(root == null) root = current;
    }
    return root;
  }
  
  public abstract T create(String seq, T current) ;
}
