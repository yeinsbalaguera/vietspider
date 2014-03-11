/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.ACTION_ASSIGNMENT;
import static org.vietspider.bean.NLPData.ACTION_SELL;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionTop2Plugin extends ActionAbstractPlugin {

  final static String[] APARTMENT = {
    //  {"giới thiệu dự án", ">9,7"},
    "căn hộ", "can ho", "chcc", "ch cao cấp", "chung cư", "chung cu", 
      "cccc", "apartment", "ch", "chcccc",/*"căn góc",*/
      "bàn giao nhà", "quyền góp vốn"
  };
  
  final static String[] IGNORE_KEYS = {
    "đối diện", "có thể xây", "tiện xây dựng"
  };
  

  public short type() { return 1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    for(int i = 0; i < APARTMENT.length; i++) {
        int index = indexOf(lower, APARTMENT[i]);
        if(index < 0) continue;
        String previous = lower.substring(0, index).trim();
        for(int k = 0; k < IGNORE_KEYS.length; k++) {
//          ActionExtractor.print(IGNORE_KEYS[k]);
//          ActionExtractor.print(previous);
//          ActionExtractor.print(previous.endsWith(IGNORE_KEYS[k]));
          if(previous.endsWith(IGNORE_KEYS[k])) {
//            ActionExtractor.print(previous);
            index = -1;
            break;
          }
        }
        if(index < 0) continue;
        
        if(lower.indexOf("nội thất") > -1
            && lower.indexOf("thiết kế") > -1) {
          index = -1;
        }
        
        if(index < 0) continue;
        
//              ActionExtractor.print(APARTMENT[i]);
//              ActionExtractor.print(lower.substring(0, index));

        putPoint(element, index, APARTMENT[i].length());

        for(int k = 0; k < ActionCommon1Plugin.ACTION_LABELS.length; k++) {
          index = indexOf(lower, ActionCommon1Plugin.ACTION_LABELS[k]);
//          ActionExtractor.print(lower );
//          ActionExtractor.print(ActionObjectCommon1Plugin.ACTION_LABELS[k] + " : "+ index);
          if(index < 0) continue;
          short action = ActionCommon1Plugin.ACTION_VALUES[k];
//                    ActionExtractor.print("===> "+ ActionObjectCommon1Plugin.ACTION_LABELS[k]);
          if(action == ACTION_ASSIGNMENT) action = ACTION_SELL;
          add(list, new ActionObject(String.valueOf(action) + ",4", APARTMENT[i]));
          putPoint(element, index, ActionCommon1Plugin.ACTION_LABELS[k].length());
          return _BREAK;
        }

        add(list, new ActionObject("1,4", APARTMENT[i]));
        return _BREAK;
    }

    return _CONTINUE;
  }



}
