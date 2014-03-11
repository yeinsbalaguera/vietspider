/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2011  
 */
public class TestText {
  public static void main(String[] args) {
    String text1 = "cao đạt";
    String text2 = "cao đạt";
    for(int i = 0; i < text1.length(); i++) {
      System.out.println(text1.charAt(i) == text2.charAt(i));
    }
    
    double total = 5000000000d;
    double unit = 65.0;
    
    double value = (total/1000000d)/unit;
    value = Math.rint(value*1000)/1000;
    System.out.println(value);
    
    System.out.println(Double.valueOf("2.1E9")/1000000);
    
    String text = "124m2";
    if(text.endsWith("m2")) {
      text = text.substring(0, text.length()-2);
      System.out.println(text);
    }
    
    System.out.println("Q.Ngũ Hành Sơn Cho thuê nhà nghỉ trọ THANH TÂM 1 địa chỉ k40/3 đư...".length());
  }
}
