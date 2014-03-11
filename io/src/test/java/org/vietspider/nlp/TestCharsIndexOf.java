/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import org.vietspider.chars.CharsUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2009  
 */
public class TestCharsIndexOf {
  public static void main(String[] args) {
    char chars [] = " Bảo hành 12 tháng, giao hàng và lắp đặt miễn phí trong phạm vi thành phố Hồ Chí Minh. ".toCharArray();
    char chars2 [] = "thành phố hồ chí minh".toCharArray();
    System.out.println(CharsUtil.indexOfIgnoreCase(chars, chars2, 0));
    
  }
}
