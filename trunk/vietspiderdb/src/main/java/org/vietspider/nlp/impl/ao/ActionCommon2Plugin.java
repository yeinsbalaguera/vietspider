/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.ACTION_LOAN;
import static org.vietspider.bean.NLPData.ACTION_RENT;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionCommon2Plugin extends ActionAbstractPlugin {

  final static String[] START_ACTION_LABELS = {
    //  "cần bán", "bán gấp", "bán", "can ban", "ban gap", "người mua", "ban", 
    //  "cho thuê", "cho thue", "for rent",
    "thuê", "thue", 
    //  "sang nhượng", "nhượng", "sang nhuong", "nhuong", "cần sang",
    //  "thanh lý", "sang lại", "sang gấp",
    //  "cần mua", "mua gấp", "can mua", "mua gap",
    //  "khóa học", "khoa hoc", "cấp chứng chỉ", "cap chung chi", 
    //  "đào tạo", "dao tao", "lớp", 
    //  "tư vấn", "tu van", 
    //  "cho vay mua nhà", "vay mua nhà"
  };

  final static short[] START_ACTION_VALUES = {
    //  ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL,
    //    ACTION_SELL,
    //  ACTION_FOR_RENT, ACTION_FOR_RENT, ACTION_FOR_RENT,
    ACTION_RENT, ACTION_RENT, 
    //  ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    //  ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    //  ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY,
    //  ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING,
    //  ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING,
    //  ACTION_CONSULTING, ACTION_CONSULTING,
    //  ACTION_LOAN, ACTION_LOAN
  };


  public short type() { return 0; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    ObjectExtractor oe = new ObjectExtractor(this);
    for(int i = 0; i < START_ACTION_LABELS.length; i++) {
      if(!lower.startsWith(START_ACTION_LABELS[i])) continue;


      if(START_ACTION_VALUES[i] == ACTION_LOAN) {
        add(list, new ActionObject("8,0", START_ACTION_LABELS[i]));
        putPoint(element, 0, START_ACTION_LABELS[i].length());
        continue;
      }

      NlpObject object = oe.extract(element, 0, START_ACTION_LABELS[i], START_ACTION_VALUES[i]);
      if(object != null) {
        StringBuilder builder = new StringBuilder();
        builder.append(START_ACTION_VALUES[i]).append(',').append(object.type);
        String action_object = builder.toString();
//        if("1,6".equals(action_object)) return false;
//        ActionExtractor.print("lower " + lower);
        add(list, new ActionObject(action_object, object.label));
        
        putPoint(element, 0, START_ACTION_LABELS[i].length());
      }
    }

    return _CONTINUE;
  }



}
