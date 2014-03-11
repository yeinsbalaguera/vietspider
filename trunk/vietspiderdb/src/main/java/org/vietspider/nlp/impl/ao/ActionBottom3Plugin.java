/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

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
public class ActionBottom3Plugin extends ActionAbstractPlugin {
  
  public short type() { return -1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();

    int index = lower.indexOf("giá");
    if(index < 0) return _CONTINUE;
    
    ObjectExtractor oe = new ObjectExtractor(this);
    
    List<Point> points = new ArrayList<Point>();
    createPoint(points, index, 3);
    NlpObject object = oe.search(points, lower, index, 3);
    //      ActionExtractor.print(object);
    if(object  == null) return _CONTINUE;
    
    StringBuilder builder = new StringBuilder();
    builder.append('<').append('1').append(',').append(object.type);
    //          ActionExtractor.print(lower);
    //            ActionExtractor.print(builder);
    add(list, new ActionObject(builder.toString(), object.label));
    for(Point p : points) {
      element.putPoint(NLPData.ACTION_OBJECT, p);
    }
    return _BREAK;

  }

}
