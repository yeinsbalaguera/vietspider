/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2011  
 */
public class PriceFilter implements INlpFilter {
  
  public static boolean TEST = false;

  private final PriceFilter1Plugin [] plugins = {
      new PriceFilter1Plugin()
  };

  public PriceFilter() {
  }

  @SuppressWarnings("unused")
  public void filter(String id, TextElement element) {
    String text = element.getLower();
    if(text == null) return;

    for(int i = 0; i < plugins.length; i++) {
      Point point =  plugins[i].filter(element, type());
//            System.out.println(i +  " : " + text + " : " + point);
      if(point != null) return;
    }
  }

  public short type() { return NLPData.PRICE; }
}
