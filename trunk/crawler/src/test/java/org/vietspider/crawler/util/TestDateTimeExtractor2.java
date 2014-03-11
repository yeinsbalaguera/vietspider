/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.vietspider.content.nlp.common.EnDateTimeExtractor;
import org.vietspider.content.nlp.common.EnDateTimeExtractor2;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.locale.DetachDate;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public class TestDateTimeExtractor2 {
  public static void main(String[] args) {
    EnDateTimeExtractor extractor = new EnDateTimeExtractor(new NodeHandler());
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy hh:mm a");
    
    String input  = "Last Updated: Monday, October 6, 2008 | 6:53 AM AT";
    
    EnDateTimeExtractor2 enDateTimeExtractor2 = new EnDateTimeExtractor2(new NodeHandler()); 
    System.out.println(enDateTimeExtractor2.cleanValue(input));
    
//    System.out.println(input.toCharArray().length);
    
    DetachDate detach = extractor.extractDateTime(input.toLowerCase());
    if(detach  == null) return ;
    Date date  = detach.toDate();
    System.out.println(detach.getScrore());  
    System.out.println(dateFormat.format(date));
    
    String compute = "cập nhật cách đây 6 ngày";
//    String compute = "24/09/2009, 09:25, am ";
//    int idx = value.indexOf(longAgo);
//    System.out.println(idx > -1 && hasTimeWord(value);
    date = extractor.computeDate(compute);
    if(date == null) return ;
    System.out.println(dateFormat.format(date));
    
//    String txt = "Hayward";
//    DetachDate detachDate = extractor.extractDateTime(txt);
//    System.out.println(detachDate.getScrore());

  }
}
