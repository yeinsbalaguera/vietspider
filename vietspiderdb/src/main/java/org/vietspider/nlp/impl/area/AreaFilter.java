/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.area;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2011  
 */
public class AreaFilter implements INlpFilter {
  
  public static boolean TEST = false;
  
  private final AreaFilterPlugin [] plugins = {
    new AreaFilter1Plugin(), new AreaFilter11Plugin(), 
    new AreaFilter2Plugin(),
    new AreaFilter3Plugin(), new AreaFilter4Plugin(), 
    new AreaFilter5Plugin(), new AreaFilter6Plugin()
    
  };

  public AreaFilter() {
  }

  @SuppressWarnings("unused")
  public void filter(String id, TextElement element) {
    String text = element.getLower();
    if(text == null) return;
    //    "diện tích", "dt: "
    
    for(int i = 0; i < plugins.length; i++) {
      Point point =  plugins[i].filter(element, type());
//      System.out.println(i +  " : " + text + " : " + point);
      if(point != null && point.getScore() > -1) return;
    }
  }
 
  public short type() { return NLPData.AREA; }
}
