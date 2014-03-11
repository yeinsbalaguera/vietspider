/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.chars;

import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 8, 2011
 */
public class TestTextSpliter {
  public static void main(String[] args) {
    TextSpliterBak spliter2 = new TextSpliterBak();
    TextSpliter spliter = new TextSpliter();
    String text = "Một nghiên cứu mới cho thấy cổ phiếu ngày càng đánh mất vai trò của mình trong các thị trường vốn toàn cầu và điều này sẽ gây ra sự chênh lệch giữa nhu cầu của nhà đầu tư và nhu ";
    
    int max = 1000000;
    char separator = 't';
    
    long start = System.currentTimeMillis();
    for(int i = 0; i < max; i++) {
      List<String> list = spliter.toList(text, separator);
    }
    long end = System.currentTimeMillis();
    System.out.println("================= > "+ (end - start));
    
    start = System.currentTimeMillis();
    for(int i = 0; i < max; i++) {
      List<String> list = spliter2.split2List(text, separator);
//      String[] list = spliter2.split2Array(text, 't');
    }
    end = System.currentTimeMillis();
    System.out.println("================= > "+ (end - start));
    
//    String[] list = spliter2.split2Array(text, 't');
//    List<String> list = spliter2.split2List(text, separator);
//    for(String ele : list) {
//      System.out.println(ele);
//    }
  }
}
