/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import org.vietspider.link.pattern.model.JavaPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 28, 2008  
 */
public class TestJavaPatternMatcher {
  
  public static void main(String[] args) throws Exception  {
    JavaPattern javaPattern = new JavaPattern();
    javaPattern.setValue("http://thanhniennews.com/*/?catid=*&newsid=*");
//    javascript:ShowNextFolderItem('2008/05/23%2000:00:00')
    String value = "http://www.thanhniennews.com/sports/?catid=5&newsid=39159";
    System.out.println(javaPattern.match(value));
  }
}
