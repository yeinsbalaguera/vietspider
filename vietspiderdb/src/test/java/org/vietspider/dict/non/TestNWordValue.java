/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.non;

import org.vietspider.db.dict.non.NWordValue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class TestNWordValue {
  public static void main(String[] args) throws Throwable {
    NWordValue nWordValue = new NWordValue();
    String text = "[tiếng Việt:1:]";
    nWordValue.fromText(text);
    System.out.println(nWordValue.toText());
    System.out.println(nWordValue.getRefs().length);
    
    text = "[tiếng Việt:0:dịch]";
    nWordValue.fromText(text);
    System.out.println(nWordValue.getRefs().length);
    System.out.println(nWordValue.toText());
    
    text = "[tiếng Việt:1:dịch;định dạng]";
    nWordValue.fromText(text);
    System.out.println(nWordValue.getRefs().length);
    System.out.println(nWordValue.toText());
    
    text = "[tiếng Việt:0:dịch;định dạng;có dấu]";
    nWordValue.fromText(text);
    System.out.println(nWordValue.getRefs().length);
    System.out.println(nWordValue.toText());
  }
}
