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
public class EmailExtractor implements INlpExtractor<String> {

  @SuppressWarnings("all")
  public void extract(String id, Collection<?> collection, TextElement element) {
    String value = element.getValue();
    HashSet<String> list = (HashSet<String>) collection;
    List<Point> points = element.getPoint(type());
    int index = points.get(0).getStart();
    while( index > 0) {
      int start = index;
      while(start > 0) {
        char c = value.charAt(start);
        if(isBreakCharacter(c)) break;
        start--;
      }

      while(start < index) {
        char c = value.charAt(start);
        if(Character.isLetterOrDigit(c)) break;
        start++;
      }

      int end = index;
      while(end < value.length()-1) {
        char c = value.charAt(end);
        if(isBreakCharacter(c)) break;
        end++;
      }

      while(end > index) {
        char c = value.charAt(end);
        if(Character.isLetterOrDigit(c)) {
          end++;
          break;
        }
        end--;
      }

      String email = value.substring(start, end);
      
      if(isValid(email)) list.add(email);
      
      if(end > value.length()-1) break;
      value = value.substring(end+1);
      index = value.indexOf('@');
    }

  }
  
  private boolean isValid(String email) {
    if(email.length() < 5) return false;
    try{
      EmailAddress.parse(email);
    } catch (Exception e) {
      return false;
    }
    
    int index = email.indexOf('@');
    if(index < 0) return false;
    index++;
    if(email.indexOf('.', index) < 0) return false;
    
    return true;
  }

  private boolean isBreakCharacter(char c) {
    if(Character.isWhitespace(c)
        || Character.isSpaceChar(c)) return true;
    if(c == ':' 
      || c == '&') return true;
    return false;
  }
  
  public boolean isExtract(TextElement element) {
    return element.getPoint(type()) != null;
  }

  public short type() { return NLPData.EMAIL; }

  public Collection<String> createCollection() { 
    return new HashSet<String>();
  }
  
  @SuppressWarnings("unused")
  public void closeCollection(Collection<?> collection) {
  }
  
  

//  public static void main(String[] args) {
//    EmailExtractor extractor = new EmailExtractor();
//    String text = "bạn..";
//    text = "www.flickr.com/photos/52410349@N07";
//    text = "củchuốithôngminh@yahoo.com";
//    System.out.println(extractor.isValid(text));
//  }
 

}
