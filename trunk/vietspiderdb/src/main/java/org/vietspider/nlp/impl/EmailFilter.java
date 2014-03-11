/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class EmailFilter implements INlpFilter {

  @SuppressWarnings("unused")
  public void filter(String id, TextElement element) {
    String value = element.getValue();
    if(value == null) return;
    int index = value.indexOf('@');
    while(index > 0) {
      element.putPoint(type(), 1, index, -1);
      index = value.indexOf('@', index+1);
    }
  }

  public short type() { return NLPData.EMAIL; }


}
