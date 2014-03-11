/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index.word;

import java.io.File;

import org.vietspider.index.analytics.Analyzer;
import org.vietspider.index.analytics.TextAnalyzer.Word;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 22, 2009  
 */
public class TestFunctionAnalyzer {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
    Analyzer analyzer = new Analyzer();
    String text = "địa    chính  thông   báo phát  hành một ứng dụng cho ";
    Word word = analyzer.searchWord(text, 0, 2);
    System.out.println(word.getValue());
    
    word = analyzer.searchWord(text, word.getEnd(), 2);
    System.out.println(word.getValue());
    
    word = analyzer.searchWord(text, word.getEnd(), 2);
    System.out.println(word.getValue());
    
  }
}
