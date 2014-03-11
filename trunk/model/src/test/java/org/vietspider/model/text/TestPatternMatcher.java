/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import org.vietspider.link.pattern.model.BeanCreator;
import org.vietspider.link.pattern.model.IPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 28, 2008  
 */
public class TestPatternMatcher {
  
  public static void main(String[] args) throws Exception  {
    IPattern pattern = BeanCreator.create("javascript:ShowNextFolderItem('*/*/*')");
//    javascript:ShowNextFolderItem('2008/05/23%2000:00:00')
    String value = "JavaScript:ShowNextFolderItem('2008/05/28 00:00:00')";
    System.out.println(pattern.match(value));
  }
}
