/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
abstract public class ActionAbstractPlugin implements ActionObjectPlugin {
  
  public void putPoint(TextElement element, int start, int length) {
    element.putPoint(NLPData.ACTION_OBJECT, -1, start, start + length);
  }

  public void createPoint(List<Point> list, int start, int length) {
    list.add(new Point(-1, start, start + length));
  }
  
  public void add(List<ActionObject> list, ActionObject ao) {
//    ActionExtractor.print(" === > "+ ao.getData());
//    if("<1,1".equals(ao.getData())) new Exception().printStackTrace();
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getData().equals(ao.getData())) return;
    }
    //  ActionExtractor.print(text);
    //  ActionExtractor.print("=======> "+ NLPData.action_object(cleanItem(data)));
    list.add(ao);
  }
  
}
