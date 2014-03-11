/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.ACTION_TRAINING;
import static org.vietspider.bean.NLPData.OBJECT_APARTMENT;
import static org.vietspider.bean.NLPData.OBJECT_ARCHITECTURE;
import static org.vietspider.bean.NLPData.OBJECT_BUSINESS;
import static org.vietspider.bean.NLPData.OBJECT_HOUSE;
import static org.vietspider.bean.NLPData.OBJECT_LAND;
import static org.vietspider.bean.NLPData.OBJECT_OFFICE;
import static org.vietspider.bean.NLPData.OBJECT_OTHER;
import static org.vietspider.bean.NLPData.OBJECT_PROJECT;
import static org.vietspider.bean.NLPData.OBJECT_ROOM;
import static org.vietspider.bean.NLPData.OBJECT_VILLA;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.searchEnd;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
class ObjectExtractor {

  final static String[] BUSINESS_LABEL = {
    "gian hàng", "cửa hàng", "quán", "kho", "xưởng", "nhà hàng",/*"nhà xưởng",*/
    "trang trại", "khách sạn", "mặt bằng", "mat bang", "shop",
    "kiot", "kiốt", "ki-ot", "ki ốt", "sạp", "quầy", "phòng net", "sân bóng đá",  
    "mỏ khai thác", "mỹ viện", "mb", "trường mầm non", "tiệm", 
    "địa điểm làm", "hecta"
  };

  final static String[] OBJECT_LABELS = {
    "nhà", "nha", "house",
    "nhà tập thể",
    
    "dự án", "du an",
    "biệt thự", "biet thu", "villa", "bt", "btcc", 

    "đất", "nền", "dat", "nen", "lô", "vườn", "thửa",
    "liền kề", "blog", "suất ngoại giao khu đô thị", "qsdđ", "dất", "bds", 


    "văn phòng", "office", "van phong", "vp", "cao ốc", "phòng làm việc", 
    "block làm việc", "floor",
    "phòng", "phong",
    "đấu thầu xây dựng", "dau thau xay dung", "bất động sản", "bat dong san",
    "quản lý dự án đầu tư xây dựng", "chứng chỉ bất động sản", "nghiệp vụ giám sát thi công",
    "chứng chỉ hành nghề kinh doanh bất động sản", "nghiệp vụ định giá xây dựng",
    "chứng chỉ đấu thầu", "quản lý dự án",
    "thiết kế", "thiet ke",
    //    "gian hàng", "cửa hàng", "quán", "kho", "xưởng", 
    //    "trang trại", "khách sạn", "mặt bằng", "mat bang", "shop",
    //    "kiot", "sạp", "quầy", "phòng net", "sân bóng đá",  
    //    "mỏ khai thác", "mỹ viện", "mb", "trường mầm non",
    "bất động sản"
  };

  final static short[] OBJECT_VALUES = {
    OBJECT_HOUSE, OBJECT_HOUSE, OBJECT_HOUSE,
    OBJECT_APARTMENT, 
    
    OBJECT_PROJECT, OBJECT_PROJECT,
    OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA, OBJECT_VILLA,
    OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND,
    OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND, OBJECT_LAND,  

    OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE, OBJECT_OFFICE,
    OBJECT_OFFICE, OBJECT_OFFICE, 
    OBJECT_ROOM, OBJECT_ROOM,
    OBJECT_OTHER, OBJECT_OTHER, OBJECT_OTHER, OBJECT_OTHER,
    OBJECT_OTHER, OBJECT_OTHER, OBJECT_OTHER,
    OBJECT_OTHER, OBJECT_OTHER,
    OBJECT_OTHER, OBJECT_OTHER,
    OBJECT_ARCHITECTURE, OBJECT_ARCHITECTURE,
    //    OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS,
    //    OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS,
    //    OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS,
    //    OBJECT_BUSINESS, OBJECT_BUSINESS, OBJECT_BUSINESS,
    OBJECT_OTHER
  };
  
  final static String[] IGNORE_KEYS = {
    "đối diện", "nhìn ra"
  };

  private  ActionAbstractPlugin plugin;

  public ObjectExtractor(ActionAbstractPlugin plugin) {
    this.plugin = plugin;
  }

  NlpObject extract(TextElement element, int index, String actionLabel, short action) {
    String lower = element.getLower();
//        ActionExtractor.println(" lower "+ lower);
    List<Point> points = new ArrayList<Point>();
    NlpObject object = search(points, lower, index, actionLabel.length());
    //    ActionExtractor.print(/*lower +*/  "  : " + action + " : " + object);
//              ActionExtractor.println(" ===  >"+ object );
    //              ));
    if(object == null) {
      //      ActionExtractor.print(lower);
      //      ActionExtractor.print(" hihi "+ ACTION_LABELS[i]);
      //      ActionExtractor.print(NLPData.action(ACTION_VALUES[i]));
      int from = index + actionLabel.length();
      for(int j = 0; j < BUSINESS_LABEL.length; j++) {
//                ActionExtractor.println(BUSINESS_LABEL[j] + " : "+ (indexOf(lower, BUSINESS_LABEL[j])));
        int index2 = indexOf(lower, BUSINESS_LABEL[j], from);
        if(index2 < 0) continue;

        //        if(!lower.startsWith(BUSINESS_LABEL[j])) continue;
        object = new NlpObject(OBJECT_BUSINESS, BUSINESS_LABEL[j]);
        plugin.createPoint(points, index2, BUSINESS_LABEL[j].length());
        break;
      }
    }

    //    ActionExtractor.print(lower);
    //    ActionExtractor.print("hihihi " + action + " : "+ object);

    if(object == null) return null;

    if(action == ACTION_TRAINING 
        && object.type != OBJECT_OTHER) return null;

    for(Point p : points) {
      element.putPoint(NLPData.ACTION_OBJECT, p);
    }
    
    return object;

    /*//          ActionExtractor.print(" object "+ object);
    StringBuilder builder = new StringBuilder();
    builder.append(action).append(',').append(object.type);
    String action_object = builder.toString();
//    if("1,6".equals(action_object)) return false;
//    ActionExtractor.print("lower " + lower);
    plugin.add(list,new ActionObject(action_object, object.label));

    return true;*/
  }

  NlpObject search(List<Point> points,
      String text, int start, int length) {
//    ActionExtractor.print(text);
    String previous = text.substring(0, start).trim();
//    ActionExtractor.println("\n\nprevious: " + previous);

    if(previous.length() > 0) {
      for(int i = 0; i < OBJECT_LABELS.length; i++) {
//        System.out.println(previous);
        if(!previous.equals(OBJECT_LABELS[i])) continue;
        plugin.createPoint(points, 0, OBJECT_LABELS[i].length());
        return new NlpObject(OBJECT_VALUES[i], OBJECT_LABELS[i]);
      }
    }

    int from = start + length;
    int end = searchEnd(text, from);
//             ActionExtractor.print(from + " : "+ end);
    String after = text.substring(from, end);
//    ActionExtractor.print("\n\nafter: "+after);
    int order = -1;
    int index = -1;
    int len = -1;
    for(int i = 0; i < OBJECT_LABELS.length; i++) {
//      if(OBJECT_LABELS[i].equals("house")) {
//        ActionExtractor.println("=======================");
//      }
      int idx = indexOf(after, OBJECT_LABELS[i]);
//      if(OBJECT_LABELS[i].equals("house")) {
//        ActionExtractor.println(text);
//        ActionExtractor.println(after);
//        ActionExtractor.println(OBJECT_LABELS[i] +  " : " + idx);
//      }
      if(idx < 0) continue;
//      if("nhà".equals(OBJECT_LABELS[i]) 
//          && idx+4 < after.length() - 1 
//          && after.substring(idx+4).startsWith("hàng")) continue;
      
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

      //       ActionExtractor.print("=======================");
      //       ActionExtractor.print(text);
      //       ActionExtractor.print(OBJECT_LABELS[i] + " thay co "+ next);

      //             ActionExtractor.print(OBJECT_LABELS[i] + " : "+ idx 
      //                 +  " : " +index+ " : " +(index > 0 && idx >= index));
      if(index > 0 && idx >= index) continue;
      order = i;
      index = idx;
      len = OBJECT_LABELS[i].length();
//            ActionExtractor.print(OBJECT_LABELS[i]);
//            ActionExtractor.print(order);
      //      return OBJECT_VALUES[i];
    }

    if(order > -1) {
      plugin.createPoint(points, from + index, len);
      return new NlpObject(OBJECT_VALUES[order], OBJECT_LABELS[order]);
//      return OBJECT_VALUES[order];
    }

    //     value = text.substring(0, start).trim();
    //         ActionExtractor.print("\n\nprevious: "+value);
    for(int i = 0; i < OBJECT_LABELS.length; i++) {
      //             ActionExtractor.print(OBJECT_LABELS[i] + " = " + value.equals(OBJECT_LABELS[i]));
//      if(!previous.startsWith(OBJECT_LABELS[i])) continue;
      if(indexOf(previous, OBJECT_LABELS[i], 0) != 0) continue;
      //             ActionExtractor.print(ACTION_LABELS[i] + " : "+ index);
      //             ActionExtractor.print(OBJECT_LABELS[i]);
      plugin.createPoint(points, 0, OBJECT_LABELS[i].length());
      return new NlpObject(OBJECT_VALUES[i], OBJECT_LABELS[i]);
//      return OBJECT_VALUES[i];
    }

    return null;
  }
  
}
