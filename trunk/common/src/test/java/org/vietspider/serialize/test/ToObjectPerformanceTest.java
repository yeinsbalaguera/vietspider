/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2011  
 */
public class ToObjectPerformanceTest {
  
 
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\article.xml");
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);

    try {
      Thread.sleep(30*1000);
    } catch (Exception e) {
      // TODO: handle exception
    }
    
    long start = System.currentTimeMillis();
    for(int i = 0; i < 100000; i++) {
      XML2Object.getInstance().toObject(Article.class, xml);
    }
    long end = System.currentTimeMillis();
    System.out.println(" objects " +  (end - start));
    
    System.exit(1);

  }

}
