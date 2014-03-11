/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.ArrayList;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionBottom2Plugin extends ActionAbstractPlugin {
  
  final static String[][] DOUBLE_WEAK = {
    {"mt", "mặt tiền"},
    {"gần", "khu", "bán gấp", "giá"},
    {"<1,1"}
  };
  
  public short type() { return -1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    for(int i = 0; i < DOUBLE_WEAK.length; i += 3) {
      String [] keys = DOUBLE_WEAK[i];
      Point point1 = null;
      for(int j = 0; j < keys.length ; j++) {
        int idx = lower.indexOf(keys[j]);
        if(idx < 0) continue;
        point1 = new Point(-1, idx, idx + keys[j].length());
        break;
      }
      if(point1 == null) continue;
      
      keys = DOUBLE_WEAK[i+1];
      Point point2 = null;
      for(int j = 0; j < keys.length; j++) {
        int idx = lower.indexOf(keys[j]);
        if(idx < 0) continue;
        point2 = new Point(-1, idx, idx + keys[j].length());
        break;
      }
      if(point2 == null) continue;
      
//      System.out.println(lower);
      
      add(list, new ActionObject(DOUBLE_WEAK[i+2][0], keys));
      element.putPoint(NLPData.ACTION_OBJECT, point1);
      element.putPoint(NLPData.ACTION_OBJECT, point2);
      return _BREAK;
    }
    
    return _CONTINUE;

  }
  
  
  
}
