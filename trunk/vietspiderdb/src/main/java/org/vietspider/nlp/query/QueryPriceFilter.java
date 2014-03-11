/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.query;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.impl.PriceFilter1Plugin;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2011  
 */
public class QueryPriceFilter extends PriceFilter1Plugin implements INlpFilter {

  @Override
  public void filter(String id, TextElement element) {
    String text = element.getLower();
    if(text == null) return;
    filter(element, type());
  }

  @Override
  public Point filter(TextElement element, short type) {
    String text = element.getLower();
    if(text == null) return null;

    int start = text.indexOf("giá");
    if(start < 0) {
      start = searchStart(text);
    }
    
    if(start < 0) return null;
    //                System.out.println(text.substring(start));
    int max = text.indexOf(". ", start+3);
    if(max < 0) {
      max = text.length();
    }

    //      point = search(text, point, start, max);
    Point point = search(text, null, start, max, true);
    if(point != null) element.putPoint(type, point);
    
    return point;
    //    
  }
  
  public boolean isPreviousValid(String previous) {
    return true;
  }

  private int searchStart(String text) {
    int start = -1;
    String [] tags = new String[] {
        "triệu", "tỷ", "trieu", "tỷ", "tỉ", "tr", "ty", "ti", "$",
        "trăm nghìn", "trăm", "nghìn", "tram nghin", "tram", "nghin"
    };

    for(int i = 0; i < tags.length; i++) {
      int index = text.indexOf(tags[i]);
      if(index < 0) continue;
      int end = index + tags[i].length();
      if(end < text.length() 
          && Character.isLetterOrDigit(text.charAt(end))) continue;
      if(start < 0 || index < start)  start = index;
    }
    
//    System.out.println(start);
    
    if(start < 0) return -1;
    
//    System.out.println(text.substring(start));
    
    start--;
    while(start > -1) {
      char c = text.charAt(start);
      if(Character.isLetter(c)) {
        start++;
        break;
      }
      start--;
    }

    return start;
  }

  @Override
  public short type() {
    return NLPData.PRICE;
  }
}
