/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2009  
 */
public class TestAddreviateDictionary {
  public static void main(String[] args) throws Exception  {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
    String text = "Cần thuê nhà mặt đường hoặc mặt ngõ to, 1 hoặc 2t, DT 25-35 m2, gần trung tâm thành phố, gần cơ quan, trường học, cho, ưu tiên hướng Bắc; Đông. LH: Mr Vương, Tel. 0936183386 ";
//    String value = AbbreviateDictionary2.getInstance().compile(text);
//    System.out.println(value);
  }
}
