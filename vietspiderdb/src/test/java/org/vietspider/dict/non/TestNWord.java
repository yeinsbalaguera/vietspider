/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.non;

import org.vietspider.db.dict.non.NWord;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class TestNWord {
  public static void main(String[] args) throws Throwable {
    NWord nWord = new NWord();
    String text = "tieng viet:[tiếng Việt:1:]";
    nWord.fromText(text);
    System.out.println(nWord.toText());
    
    text = "tieng viet:[tiếng Việt:0:dịch]";
    nWord.fromText(text);
    System.out.println(nWord.toText());
    
    text = "tieng viet:[tiếng Việt:1:dịch;định dạng][tiếng viết:0:chữ]";
    nWord.fromText(text);
    System.out.println(nWord.toText());
  }
}
