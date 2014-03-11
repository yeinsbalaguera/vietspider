/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.area;

import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class AreaFilter4Plugin extends  AreaFilterPlugin  {

  Point filter(TextElement element, short type) {
    String text = element.getLower();
    if(text.trim().isEmpty()) return null;
    int index = 0;
    while(index < text.length()) {
//      char c = text.charAt(index);
//      if(isDigitValue(c)
//            || c == '(' || c == ')'
//              || c == 'm' || c == '²') {
      if(isAreaValue(text, index)) {
        index++;
        continue;
      }
      return null;
    }
    TextElement previous = previous(element);
//    System.out.println(text +  " : "+ previous);
    if(previous == null) return null;
    text = previous.getLower();
    if(text.indexOf("diện tích") < 0) return null;
    if(AreaFilter.TEST) {
      System.out.println(" filter 4: "+ text + "=" + element.getValue()+ " : "+ score(text));
    }
    Point point = new Point(score(text), 0, element.getValue().length());
    element.putPoint(type, point);
    return point;
  }
}
