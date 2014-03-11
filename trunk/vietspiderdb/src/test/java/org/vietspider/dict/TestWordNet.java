/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict;

import java.io.File;

import org.vietspider.db.dict.VietnameseDictionary;
import org.vietspider.db.dict.WordIndex2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestWordNet {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    WordIndex2 wordIndex2 = VietnameseDictionary.getInstance().getWordIndex();
    
    String word = "chuyên nghiệp"; 
    System.out.println(VietnameseDictionary.getInstance().contains("chia sẻ"));
    System.out.println(wordIndex2.contains(word));
//    wordIndex.add(word);
    System.out.println(wordIndex2.contains(word));
//    System.out.println("=========================================");
    System.out.println(wordIndex2.toString());
    
//    System.out.println(wordIndex.toString());
  }
}
