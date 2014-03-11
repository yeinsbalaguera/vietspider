/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.ACTION_ASSIGNMENT;
import static org.vietspider.bean.NLPData.ACTION_BUY;
import static org.vietspider.bean.NLPData.ACTION_CONSULTING;
import static org.vietspider.bean.NLPData.ACTION_FOR_RENT;
import static org.vietspider.bean.NLPData.ACTION_LOAN;
import static org.vietspider.bean.NLPData.ACTION_RENT;
import static org.vietspider.bean.NLPData.ACTION_SELL;
import static org.vietspider.bean.NLPData.ACTION_TRAINING;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionCommon1Plugin extends ActionAbstractPlugin {
  
  final public static String[] ACTION_LABELS = {
    "cần bán", "bán gấp", "bán", "can ban", "ban gap", "người mua", "ban", 
    //    "giá", 
    "cho thuê", "cho thue", "for rent",
    "cần thuê", "can thue", "nhu cầu thuê", "tìm thuê",
    "sang nhượng", "nhượng", "sang nhuong", "nhuong", "cần sang",
    "thanh lý", "sang lại", "sang gấp",
    "cần mua", "mua gấp", "can mua", "mua gap", "có nhu cầu mua", "cần tìm mua", 
    "khóa học", "khoa hoc", "cấp chứng chỉ", "cap chung chi", 
    "đào tạo", "dao tao", "lớp", 
    "tư vấn", "tu van", 
    "cho vay mua nhà", "vay mua nhà"
  };

  final public static short[] ACTION_VALUES = {
    ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL,
    //    ACTION_SELL,
    ACTION_FOR_RENT, ACTION_FOR_RENT, ACTION_FOR_RENT,
    ACTION_RENT, ACTION_RENT, ACTION_RENT, ACTION_RENT,
    ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY,
    ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING,
    ACTION_TRAINING, ACTION_TRAINING, ACTION_TRAINING,
    ACTION_CONSULTING, ACTION_CONSULTING,
    ACTION_LOAN, ACTION_LOAN
  };
  
  
  public short type() { return 0; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    ObjectExtractor oe = new ObjectExtractor(this);    
    for(int i = 0; i < ACTION_LABELS.length; i++) {
      int index = indexOf(lower, ACTION_LABELS[i]);
      if(index < 0) continue;
      
//      if(ACTION_VALUES[i] == ACTION_FOR_RENT) {
//        ActionExtractor.print(lower);
//        ActionExtractor.print(ACTION_LABELS[i] + " : "+ index);
//      }
      
      if(ACTION_VALUES[i] == ACTION_LOAN) {
        //        ActionExtractor.print(lower);
        add(list, new ActionObject("8,0", ACTION_LABELS[i]));
        putPoint(element, index, ACTION_LABELS[i].length());
        continue;
      }
      
//      ActionExtractor.print(ACTION_LABELS[i] + " : " + lower);
      
      NlpObject object = oe.extract(element, index, ACTION_LABELS[i], ACTION_VALUES[i]);
//      ActionExtractor.print(object);
      if(object != null) {
        StringBuilder builder = new StringBuilder();
        builder.append(ACTION_VALUES[i]).append(',').append(object.type);
        String action_object = builder.toString();
//        if("1,6".equals(action_object)) return false;
//        ActionExtractor.print("lower " + lower);
//        ActionExtractor.print(ACTION_LABELS[i] + " , " + ACTION_VALUES[i]);
//        ActionExtractor.print(object.label);
        add(list, new ActionObject(action_object, object.label));
        
        putPoint(element, index, ACTION_LABELS[i].length());
      }
    }
    
    return _CONTINUE;
  }
  
  
  
}
