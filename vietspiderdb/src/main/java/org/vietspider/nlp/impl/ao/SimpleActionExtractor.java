/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import static org.vietspider.bean.NLPData.ACTION_ASSIGNMENT;
import static org.vietspider.bean.NLPData.ACTION_BUY;
import static org.vietspider.bean.NLPData.ACTION_FOR_RENT;
import static org.vietspider.bean.NLPData.ACTION_RENT;
import static org.vietspider.bean.NLPData.ACTION_SELL;
import static org.vietspider.nlp.impl.ao.AOTextProcessor.indexOf;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class SimpleActionExtractor {
  
  final static String[] ACTION_LABELS = {
    "cần bán", "bán gấp", "bán", "can ban", "ban gap", "người mua", "ban", 
    //    "giá", 
    "cho thuê", "cho thue", "for rent",
    "cần thuê", "can thue", 
    "sang nhượng", "nhượng", "sang nhuong", "nhuong", "cần sang",
    "thanh lý", "sang lại", "sang gấp",
    "cần mua", "mua gấp", "can mua", "mua gap", "có nhu cầu mua"
  };

  final static short[] ACTION_VALUES = {
    ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL, ACTION_SELL,
    //    ACTION_SELL,
    ACTION_FOR_RENT, ACTION_FOR_RENT, ACTION_FOR_RENT,
    ACTION_RENT, ACTION_RENT, 
    ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    ACTION_ASSIGNMENT, ACTION_ASSIGNMENT, ACTION_ASSIGNMENT,
    ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY, ACTION_BUY
  };
  
  
  public NlpAction extract(TextElement element) {
    String lower = element.getLower();
    
    for(int i = 0; i < ACTION_LABELS.length; i++) {
      int index = indexOf(lower, ACTION_LABELS[i]);
      if(index < 0) continue;
      
      NlpAction action  = new NlpAction(ACTION_VALUES[i], ACTION_LABELS[i]);
      action.setStart(index);
      
      return action;
    }
    
    return null;
  }
  
}
