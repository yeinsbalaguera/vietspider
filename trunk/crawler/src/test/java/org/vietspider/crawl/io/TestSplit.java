/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 5, 2008  
 */
public class TestSplit {
  
  public static void main(String[] args) {
    String text = "'Hà Nội đê khó trụ vững nếu lại mưa lớn'";
    Pattern pattern = Pattern.compile("\\w{đê}", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);
    System.out.println(matcher.find());
  }
  
}
