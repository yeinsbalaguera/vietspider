/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;

import org.vietspider.common.io.RWData;
import org.vietspider.common.text.TextCounter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 18, 2009  
 */
public class TestScore {
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\web\\score\\0.txt");
    byte [] data = RWData.getInstance().load(file);
    String value = new String(data, "utf-8");
    
    int start = -1;
    int index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == '\\') {
        if(start < 0) start = index;
      } else {
        if(start >= 0) break;
      }
      index++;
    }
    
   ScoreCalculator calculator = new ScoreCalculator();
    
    String pattern = value.substring(start, index);
    TextCounter textCounter = new TextCounter();
    int sentence = textCounter.countSentence(value, 0, start);
    int word = textCounter.countWord(value, 0, start);
    
    System.out.println(pattern.length() + " : " + sentence+ " : "+ word);
    System.out.println(calculator.calculate(pattern.length(), sentence, word));
    
    sentence = textCounter.countSentence(value, index, value.length());
    word = textCounter.countWord(value, index, value.length());
    
    System.out.println(pattern.length() + " : " + sentence+ " : "+ word);
    System.out.println(calculator.calculate(pattern.length(), sentence, word));
  }
} 
