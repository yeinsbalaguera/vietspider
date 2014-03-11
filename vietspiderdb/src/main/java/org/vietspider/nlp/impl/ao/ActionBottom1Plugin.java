/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionBottom1Plugin extends ActionAbstractPlugin {
  
  final static String[][] WEAK = {
    {"dự án", "<9,7"}, 
    {"nhà ", "trệt", "tầng", "cấp 4", "phòng ngủ", "<1,1"},
    {"khu đô thị", "khu do thi", "đtm", "<9,7"},
    {"đất", "đât", "lô", "sổ đỏ", "sđỏ", "liền kề", "nền",  "<1,2"},
    {"biệt thự", "<1,5"}
  };
  
  public short type() { return -1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    for(int i = 0; i < WEAK.length; i++) {
      String [] keys = WEAK[i];
      for(int j = 0; j < keys.length - 1; j++) {
        int idx = indexOf(lower, keys[j], 0);
        if(idx < 0) continue;
        if(lower.indexOf("địa chỉ:") > -1) continue;
//        ActionExtractor.print(keys[j] + " : " + keys[keys.length -1]);
        add(list, new ActionObject(keys[keys.length -1], keys[j]));
        putPoint(element, idx, keys[j].length());
        return _BREAK;
      }
    }
    
    return _CONTINUE;

  }
  
  
  
}
