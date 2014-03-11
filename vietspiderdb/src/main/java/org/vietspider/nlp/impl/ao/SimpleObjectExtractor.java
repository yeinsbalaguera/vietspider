/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.OBJECT_APARTMENT;
import static org.vietspider.bean.NLPData.OBJECT_BUSINESS;
import static org.vietspider.bean.NLPData.OBJECT_HOUSE;
import static org.vietspider.bean.NLPData.OBJECT_LAND;
import static org.vietspider.bean.NLPData.OBJECT_OFFICE;
import static org.vietspider.bean.NLPData.OBJECT_ROOM;
import static org.vietspider.bean.NLPData.OBJECT_VILLA;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.searchEnd;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2011  
 */
public class SimpleObjectExtractor {
  
  final static String[] BUSINESS_LABEL = {
    "gian hàng", "cửa hàng", "quán", "kho", "xưởng", "nhà hàng",/*"nhà xưởng",*/
    "trang trại", "khách sạn", "mặt bằng", "mat bang", "shop",
    "kiot", "kiốt", "ki-ot", "ki ốt", "sạp", "quầy", "phòng net", "sân bóng đá",  
    "mỏ khai thác", "mỹ viện", "mb", "trường mầm non", "tiệm", 
    "địa điểm làm", "hecta"
  };

  final static String[] OBJECT_LABELS = {
    "nhà", "nha", "house",
    "biệt thự", "biet thu", "villa", "bt", "btcc", 

    "đất", "nền", "dat", "nen", "lô", "vườn", "thửa",
    "liền kề", "blog", "suất ngoại giao khu đô thị", "qsdđ", "dất", "bds", 


    "văn phòng", "office", "van phong", "vp", "cao ốc", "phòng làm việc", 
    "block làm việc", 
    "phòng", "phong",
    
    "căn hộ", "can ho", "chcc", "ch cao cấp", "chung cư",
    "chung cu", "cccc", "apartment", "ch", "chcccc"
  };

  final static short[] OBJECT_VALUES = {
    OBJECT_HOUSE, OBJECT_HOUSE, OBJECT_HOUSE,
    OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA,
    
    OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND,
    OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND,  

    OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE,
    OBJECT_OFFICE, 
    OBJECT_ROOM, OBJECT_ROOM,
    
    OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT,
    OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT, OBJECT_APARTMENT 
  };
  
  final static String[] IGNORE_KEYS = {
    "đối diện", "nhìn ra"
  };

  public SimpleObjectExtractor() {
  }

  public NlpObject extract(TextElement element) {
    String lower = element.getLower();
    NlpObject object = search(lower);
    if(object == null) {
      int from = 0;
      for(int j = 0; j < BUSINESS_LABEL.length; j++) {
//        ActionExtractor.print(BUSINESS_LABEL[j] + " : "+ (indexOf(lower, BUSINESS_LABEL[j])));
        int index2 = indexOf(lower, BUSINESS_LABEL[j], from);
        if(index2 < 0) continue;

        //        if(!lower.startsWith(BUSINESS_LABEL[j])) continue;
        object = new NlpObject(OBJECT_BUSINESS, BUSINESS_LABEL[j]);
        object.setStart(index2);
        break;
      }
    }

    return object;
  }

  NlpObject search(String text) {
    int from = 0;
    int end = searchEnd(text, from);
    //         ActionExtractor.print(from + " : "+ end);
    String after = text.substring(from, end);
//    ActionExtractor.print("\n\nafter: "+after);
    int order = -1;
    int index = -1;
    for(int i = 0; i < OBJECT_LABELS.length; i++) {
      int idx = indexOf(after, OBJECT_LABELS[i]);
      if(idx < 0) continue;
      
//      ActionExtractor.print(OBJECT_LABELS[i] + " : " + idx);
      
      String preText = after.substring(0, idx); 
      int last = preText.lastIndexOf(',');
      if(last < 0) last = preText.lastIndexOf('.');
      if(last < 0) last = preText.lastIndexOf('!');
      if(last < 0) last = preText.lastIndexOf('?');
      if(last > 0) preText = preText.substring(last+1);
//      ActionExtractor.print(" ================ >" + last+ " : "+preText);
      for(int k = 0; k < IGNORE_KEYS.length; k++) {
        if(preText.indexOf(IGNORE_KEYS[k]) > -1) {
          //        ActionExtractor.print(previous);
          idx = -1;
          break;
        }
      }
      if(idx < 0) continue;

      if(index > 0 && idx >= index) continue;
      order = i;
      index = idx;
    }

    if(order > -1) {
      NlpObject object = new NlpObject(OBJECT_VALUES[order], OBJECT_LABELS[order]);
      object.setStart(index);
      return object;
    }

    return null;
  }
}
