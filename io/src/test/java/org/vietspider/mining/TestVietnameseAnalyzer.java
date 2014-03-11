/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.mining;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.content.tp.vn.VietnameseAnalyzer;
import org.vietspider.content.tp.word.Word;
import org.vietspider.content.tp.word.WordList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class TestVietnameseAnalyzer {
  
  static File folder = new File("D:\\Temp\\articles\\analyzer\\");
  
  static void print(WordList list, String name)  throws Exception {
    StringBuilder builder = new StringBuilder();
    Word [] words = list.getWord();
    for(Word word : words) {
      if(builder.length() > 0) builder.append("\n");
      builder.append(word.getValue()).append(" : ").append(word.getTime());      
    }   
    org.vietspider.common.io.RWData.getInstance().save(new File(folder, name+".txt"), builder.toString().getBytes("utf-8"));
  }
  
  static void test(String text) throws Exception {
    WordList sections = new WordList(100);
    WordList nouns = new WordList(50);
    
    VietnameseAnalyzer analyzer = new VietnameseAnalyzer();
    analyzer.breakBroken(text, sections, nouns);
    
    print(sections, "sections");
    print(nouns, "nouns");
  }
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\Temp\\articles\\text\\200908181120370.txt");
    String text = new String(RWData.getInstance().load(file), "utf-8");
    
    test(text);
  }
}
