/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionTop1Plugin extends ActionAbstractPlugin {
  
  public short type() { return 1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    String intro = "giới thiệu dự án";
    if(lower.startsWith(intro)) {
      list.clear();
      list.add(new ActionObject(">9,7", intro));
      putPoint(element, 0, intro.length());
      return _BREAK;
    }
    
    return _CONTINUE;

  }
  
  
  
}
