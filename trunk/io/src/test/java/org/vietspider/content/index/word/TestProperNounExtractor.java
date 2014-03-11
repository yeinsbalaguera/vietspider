/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index.word;

import org.vietspider.index.word.ProperNounExtractor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestProperNounExtractor {
  public static void main(String[] args) {
    ProperNounExtractor extractor = new ProperNounExtractor();
    String text = "ở đội 1 thôn Quảng Minh xã Mỹ Hưng huyen Thanh Oai  tp Hà Nội phản ánh việc Chính Quyền Xã lấy gạo cứu trợ của dân trong trận lụt tháng 10/2008";
    /*List<String> nouns = new ArrayList<String>();
    List<String> sequances = extractor.extract(nouns, text);
    
    for (int i = 0; i < nouns.size(); i++) {
      System.out.println(nouns.get(i));
    }
    
    System.out.println("==================================");
    
    for (int i = 0; i < sequances.size(); i++) {
      System.out.println(sequances.get(i));
    }*/
  }
}
